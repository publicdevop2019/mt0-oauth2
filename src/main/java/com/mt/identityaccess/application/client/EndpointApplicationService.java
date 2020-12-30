package com.mt.identityaccess.application.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mt.common.application.SubscribeForEvent;
import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.common.rest.exception.AggregatePatchException;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.client.command.EndpointCreateCommand;
import com.mt.identityaccess.application.client.command.EndpointPatchCommand;
import com.mt.identityaccess.application.client.command.EndpointUpdateCommand;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.Endpoint;
import com.mt.identityaccess.domain.model.client.EndpointId;
import com.mt.identityaccess.domain.model.client.event.EndpointCreated;
import com.mt.identityaccess.domain.model.client.event.EndpointDeleted;
import com.mt.identityaccess.domain.model.client.event.EndpointUpdated;
import com.mt.identityaccess.domain.model.client.event.EndpointsBatchDeleted;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EndpointApplicationService {
    public static final String EXCHANGE_RELOAD_EP_CACHE = "reloadEpCache";
    @Autowired
    private ObjectMapper om;

    @SubscribeForEvent
    @Transactional
    public EndpointId create(EndpointCreateCommand command, String changeId) {
        EndpointId endpointId = new EndpointId();
        return (EndpointId) ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, changeId, endpointId, () -> {
            String s = "clientId:" + command.getResourceId() + ",path:" + command.getPath() + ",method:" + command.getMethod();
            SumPagedRep<Endpoint> endpointSumPagedRep = DomainRegistry.endpointRepository().endpointsOfQuery(new EndpointQuery(s), new EndpointPaging(), new QueryConfig("sc:1"));
            if (!endpointSumPagedRep.getData().isEmpty())
                throw new DuplicateEndpointException();
            String resourceId = command.getResourceId();
            Endpoint endpoint = new Endpoint(command.getExpression(), command.getDescription(), new ClientId(resourceId), command.getPath(), endpointId, command.getMethod());
            DomainRegistry.endpointRepository().add(endpoint);
            DomainEventPublisher.instance().publish(new EndpointCreated(endpointId));
            return endpointId;
        });
    }

    @Transactional(readOnly = true)
    public SumPagedRep<Endpoint> endpoints(String queryParam, String pageParam, String config) {
        return DomainRegistry.endpointRepository().endpointsOfQuery(new EndpointQuery(queryParam), new EndpointPaging(pageParam), new QueryConfig(config));
    }

    @Transactional(readOnly = true)
    public Optional<Endpoint> endpoint(String id) {
        return DomainRegistry.endpointRepository().endpointOfId(new EndpointId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void update(String id, EndpointUpdateCommand command, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
        if (endpoint.isPresent()) {
            Endpoint endpoint1 = endpoint.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
                endpoint1.replace(
                        command.getExpression(),
                        command.getDescription(),
                        command.getPath(),
                        command.getMethod()
                );
                DomainRegistry.endpointRepository().add(endpoint1);
            });
        }
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoint(String id, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
        if (endpoint.isPresent()) {
            Endpoint endpoint1 = endpoint.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(null, changeId, (ignored) -> {
                DomainRegistry.endpointRepository().remove(endpoint1);
                DomainEventPublisher.instance().publish(new EndpointDeleted(endpointId));
            });
        }
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoints(String queryParam, String changeId) {
        List<Endpoint> endpoints = DomainRegistry.endpointService().getEndpointsOfQuery(new EndpointQuery(queryParam));
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, changeId, (ignored) -> {
            DomainRegistry.endpointRepository().remove(endpoints);
            DomainEventPublisher.instance().publish(
                    new EndpointsBatchDeleted(endpoints.stream().map(Endpoint::getEndpointId).collect(Collectors.toSet()))
            );
        });
    }

    @SubscribeForEvent
    @Transactional
    public void patchEndpoint(String id, JsonPatch command, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
        if (endpoint.isPresent()) {
            Endpoint endpoint1 = endpoint.get();
            EndpointPatchCommand middleLayer = new EndpointPatchCommand(endpoint1);
            try {
                JsonNode jsonNode = om.convertValue(middleLayer, JsonNode.class);
                JsonNode patchedNode = command.apply(jsonNode);
                middleLayer = om.treeToValue(patchedNode, EndpointPatchCommand.class);
            } catch (JsonPatchException | JsonProcessingException e) {
                log.error("error during patching", e);
                throw new AggregatePatchException();
            }
            EndpointPatchCommand finalMiddleLayer = middleLayer;
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
                endpoint1.replace(
                        finalMiddleLayer.getExpression(),
                        finalMiddleLayer.getDescription(),
                        finalMiddleLayer.getMethod(),
                        finalMiddleLayer.getPath()
                );
            });
        }
    }

    public void reloadInProxy(Object o) {
        if (
                o instanceof EndpointUpdated ||
                        o instanceof EndpointCreated ||
                        o instanceof EndpointDeleted ||
                        o instanceof EndpointsBatchDeleted
        ) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.exchangeDeclare(EXCHANGE_RELOAD_EP_CACHE, "fanout");
                channel.basicPublish(EXCHANGE_RELOAD_EP_CACHE, "", null, null);
                log.debug("sent clean filter cache message");
            } catch (IOException | TimeoutException e) {
                log.error("error in mq", e);
            }
        }
    }
}
