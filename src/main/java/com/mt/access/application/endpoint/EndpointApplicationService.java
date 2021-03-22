package com.mt.access.application.endpoint;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.model.domain_event.*;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.endpoint.command.EndpointCreateCommand;
import com.mt.access.application.endpoint.command.EndpointPatchCommand;
import com.mt.access.application.endpoint.command.EndpointUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.endpoint.event.EndpointCollectionModified;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EndpointApplicationService {
    @Value("${proxy.reload}")
    private boolean reloadOnAppStart;
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    protected void reloadProxy() {
        if (reloadOnAppStart) {
            log.debug("sending reload proxy endpoint message");
            DomainRegistry.eventStreamService().next(appName, false, "system", new AppStarted());
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
                Endpoint endpoint = client1.addNewEndpoint(
                        command.getUserRoles(),
                        command.getClientRoles(),
                        command.getClientScopes(),
                        command.getDescription(),
                        command.getPath(),
                        endpointId,
                        command.getMethod(),
                        command.isSecured(),
                        command.isUserOnly(),
                        command.isClientOnly()
                );
                DomainRegistry.endpointRepository().add(endpoint);
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
                return endpointId;
            } else {
                throw new InvalidClientIdException();
            }
        }, Endpoint.class);
    }

    public SumPagedRep<Endpoint> endpoints(String queryParam, String pageParam, String config) {
        return DomainRegistry.endpointRepository().endpointsOfQuery(new EndpointQuery(queryParam, pageParam, config));
    }

    public Optional<Endpoint> endpoint(String id) {
        return DomainRegistry.endpointRepository().endpointOfId(new EndpointId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void update(String id, EndpointUpdateCommand command, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(endpointId, command, changeId, (ignored) -> {
            Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                endpoint1.replace(
                        command.getUserRoles(),
                        command.getClientRoles(),
                        command.getClientScopes(),
                        command.getDescription(),
                        command.getPath(),
                        command.getMethod(),
                        command.isSecured(),
                        command.isUserOnly(),
                        command.isClientOnly()
                );
                DomainRegistry.endpointRepository().add(endpoint1);
            }
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoint(String id, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(endpointId, null, changeId, (ignored) -> {
            Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                DomainRegistry.endpointRepository().remove(endpoint1);
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
            }
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoints(String queryParam, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, null, changeId, (change) -> {
            Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.endpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery(queryParam));
            change.setDeletedIds(allByQuery.stream().map(e -> e.getEndpointId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            DomainRegistry.endpointRepository().remove(allByQuery);
            DomainEventPublisher.instance().publish(
                    new EndpointCollectionModified()
            );
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patchEndpoint(String id, JsonPatch command, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(endpointId, command, changeId, (ignored) -> {
            Optional<Endpoint> endpoint = DomainRegistry.endpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                EndpointPatchCommand beforePatch = new EndpointPatchCommand(endpoint1);
                EndpointPatchCommand afterPatch = DomainRegistry.customObjectSerializer().applyJsonPatch(command, beforePatch, EndpointPatchCommand.class);
                endpoint1.replace(
                        endpoint1.getUserRoles(),
                        endpoint1.getClientRoles(),
                        endpoint1.getClientScopes(),
                        afterPatch.getDescription(),
                        afterPatch.getPath(),
                        afterPatch.getMethod(),
                        endpoint1.isSecured(),
                        endpoint1.isUserOnly(),
                        endpoint1.isClientOnly()
                );
            }
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void reloadEndpointCache(String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, null, changeId, (ignored) -> {
            DomainRegistry.endpointService().reloadEndpointCache();
        }, Endpoint.class);
    }

    @SubscribeForEvent
    @Transactional
    public void handleChange(StoredEvent event) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, null, event.getId().toString(), (ignored) -> {
            if (ClientDeleted.class.getName().equals(event.getName())) {
                DomainEvent deserialize = DomainRegistry.customObjectSerializer().deserialize(event.getEventBody(), DomainEvent.class);
                Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.endpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery(deserialize.getDomainId()));
                if (!allByQuery.isEmpty()) {
                    DomainRegistry.endpointRepository().remove(allByQuery);
                    DomainEventPublisher.instance().publish(new EndpointCollectionModified());
                }
            }
        }, Endpoint.class);
    }

    public static class InvalidClientIdException extends RuntimeException {
    }
}
