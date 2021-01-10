package com.mt.identityaccess.application.endpoint;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.domain_event.StoredEvent;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.persistence.QueryConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.client.InvalidClientIdException;
import com.mt.identityaccess.application.endpoint.command.EndpointCreateCommand;
import com.mt.identityaccess.application.endpoint.command.EndpointPatchCommand;
import com.mt.identityaccess.application.endpoint.command.EndpointUpdateCommand;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.domain.model.endpoint.EndpointId;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointCreated;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointDeleted;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointUpdated;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointsBatchDeleted;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EndpointApplicationService {
    public static final String EXCHANGE_RELOAD_EP_CACHE = "reloadEpCache";
    @Value("${proxy.reload}")
    private boolean reloadOnAppStart;

    @EventListener(ApplicationReadyEvent.class)
    protected void reloadProxy() {
        if (reloadOnAppStart) {
            log.debug("sending reload proxy endpoint message");
            reloadInProxy();
        }
    }

    @SubscribeForEvent
    @Transactional
    public String create(EndpointCreateCommand command, String changeId) {
        EndpointId endpointId = new EndpointId();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, changeId, endpointId, () -> {
            String resourceId = command.getResourceId();
            Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(resourceId));
            if (client.isPresent()) {
                Client client1 = client.get();
                Endpoint endpoint = client1.addNewEndpoint(command.getExpression(), command.getDescription(), command.getPath(), endpointId, command.getMethod());
                DomainRegistry.endpointRepository().add(endpoint);
                DomainEventPublisher.instance().publish(new EndpointCreated(endpointId));
                return endpointId;
            } else {
                throw new InvalidClientIdException();
            }
        }, Endpoint.class);
    }

    public SumPagedRep<Endpoint> endpoints(String queryParam, String pageParam, String config) {
        return DomainRegistry.endpointRepository().endpointsOfQuery(new EndpointQuery(queryParam), new EndpointPaging(pageParam), new QueryConfig(config));
    }

    public Optional<Endpoint> endpoint(String id) {
        return DomainRegistry.endpointRepository().endpointOfId(new EndpointId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void update(String id, EndpointUpdateCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            EndpointId endpointId = new EndpointId(id);
            Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                endpoint1.replace(
                        command.getExpression(),
                        command.getDescription(),
                        command.getPath(),
                        command.getMethod()
                );
                DomainRegistry.endpointRepository().add(endpoint1);
            }
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoint(String id, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, changeId, (ignored) -> {
            EndpointId endpointId = new EndpointId(id);
            Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                DomainRegistry.endpointRepository().remove(endpoint1);
                DomainEventPublisher.instance().publish(new EndpointDeleted(endpointId));
            }
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoints(String queryParam, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, changeId, (command) -> {
            List<Endpoint> endpoints = DomainRegistry.endpointService().getEndpointsOfQuery(new EndpointQuery(queryParam));
            command.setRequestBody(endpoints);
            DomainRegistry.endpointRepository().remove(endpoints);
            DomainEventPublisher.instance().publish(
                    new EndpointsBatchDeleted(endpoints.stream().map(Endpoint::getEndpointId).collect(Collectors.toSet()))
            );
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patchEndpoint(String id, JsonPatch command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            EndpointId endpointId = new EndpointId(id);
            Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                EndpointPatchCommand beforePatch = new EndpointPatchCommand(endpoint1);
                EndpointPatchCommand afterPatch = DomainRegistry.customObjectSerializer().applyJsonPatch(command, beforePatch, EndpointPatchCommand.class);
                endpoint1.replace(
                        afterPatch.getExpression(),
                        afterPatch.getDescription(),
                        afterPatch.getMethod(),
                        afterPatch.getPath()
                );
            }
        }, Endpoint.class);
    }

    private static final Set<String> EVENTS = new HashSet<>();

    static {
        EVENTS.add(EndpointUpdated.class.getName());
        EVENTS.add(EndpointCreated.class.getName());
        EVENTS.add(EndpointDeleted.class.getName());
        EVENTS.add(EndpointsBatchDeleted.class.getName());
    }

    public void reloadInProxy(StoredEvent o) {
        if (EVENTS.contains(o.getName())) {
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

    public void reloadInProxy() {
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
