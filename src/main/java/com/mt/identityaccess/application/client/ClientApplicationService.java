package com.mt.identityaccess.application.client;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain_event.DomainEvent;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.domain_event.StoredEvent;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.persistence.QueryConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.client.command.ClientCreateCommand;
import com.mt.identityaccess.application.client.command.ClientPatchCommand;
import com.mt.identityaccess.application.client.command.ClientUpdateCommand;
import com.mt.identityaccess.application.client.representation.ClientSpringOAuth2Representation;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.*;
import com.mt.identityaccess.domain.model.client.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientApplicationService implements ClientDetailsService {
    private static final Set<String> EVENTS = new HashSet<>();

    static {
        EVENTS.add(ClientAccessibleChanged.class.getName());
        EVENTS.add(ClientAccessTokenValiditySecondsChanged.class.getName());
        EVENTS.add(ClientAuthoritiesChanged.class.getName());
        EVENTS.add(ClientGrantTypeChanged.class.getName());
        EVENTS.add(ClientRefreshTokenChanged.class.getName());
        EVENTS.add(ClientDeleted.class.getName());
        EVENTS.add(ClientResourcesChanged.class.getName());
        EVENTS.add(ClientScopesChanged.class.getName());
        EVENTS.add(ClientSecretChanged.class.getName());
    }

    @SubscribeForEvent
    @Transactional
    public String create(ClientCreateCommand command, String operationId) {
        ClientId clientId = DomainRegistry.clientRepository().nextIdentity();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, clientId,
                () -> {
                    RefreshTokenGrant refreshTokenGrantDetail = new RefreshTokenGrant(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds());
                    PasswordGrant passwordGrantDetail = new PasswordGrant(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds(), refreshTokenGrantDetail);
                    return DomainRegistry.clientService().create(
                            clientId,
                            command.getName(),
                            command.getClientSecret(),
                            command.getDescription(),
                            command.isResourceIndicator(),
                            command.getScopeEnums(),
                            command.getGrantedAuthorities(),
                            command.getResourceIds() != null ? command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.EMPTY_SET,
                            new ClientCredentialsGrant(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds()),
                            passwordGrantDetail,
                            new AuthorizationCodeGrant(
                                    command.getGrantTypeEnums(),
                                    command.getRegisteredRedirectUri(),
                                    command.isAutoApprove(),
                                    command.getAccessTokenValiditySeconds()
                            )
                    );
                }, Client.class
        );

    }

    public SumPagedRep<Client> clients(String queryParam, String pagingParam, String configParam) {
        return DomainRegistry.clientRepository().clientsOfQuery(new ClientQuery(queryParam), new ClientPaging(pagingParam), new QueryConfig(configParam));
    }

    public Optional<Client> client(String id) {
        return DomainRegistry.clientRepository().clientOfId(new ClientId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replaceClient(String id, ClientUpdateCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            ClientId clientId = new ClientId(id);
            Optional<Client> optionalClient = DomainRegistry.clientRepository().clientOfId(clientId);
            if (optionalClient.isPresent()) {
                Client client = optionalClient.get();
                RefreshTokenGrant refreshTokenGrantDetail = new RefreshTokenGrant(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds());
                client.replace(
                        command.getName(),
                        command.getClientSecret(),
                        command.getDescription(),
                        command.isResourceIndicator(),
                        command.getScopeEnums(),
                        command.getGrantedAuthorities(),
                        command.getResourceIds() != null ? command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.EMPTY_SET,
                        new ClientCredentialsGrant(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds()),
                        new PasswordGrant(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds(), refreshTokenGrantDetail),
                        new AuthorizationCodeGrant(
                                command.getGrantTypeEnums(),
                                command.getRegisteredRedirectUri(),
                                command.isAutoApprove(),
                                command.getAccessTokenValiditySeconds()
                        )
                );
                DomainRegistry.clientRepository().add(client);
            }
        }, Client.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeClient(String id, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(id, changeId, (change) -> {
            ClientId clientId = new ClientId(id);
            Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
            if (client.isPresent()) {
                Client client1 = client.get();
                if (client1.isNonRoot()) {
                    DomainRegistry.clientRepository().remove(client1);
                    DomainEventPublisher.instance().publish(new ClientDeleted(clientId));
                } else {
                    throw new RootClientDeleteException();
                }
            }
        }, Client.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeClients(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(null, changeId, (change) -> {
            List<Client> allClientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(queryParam));
            boolean b = allClientsOfQuery.stream().anyMatch(e -> !e.isNonRoot());
            if (!b) {
                change.setRequestBody(allClientsOfQuery);
                DomainRegistry.clientRepository().remove(allClientsOfQuery);
                DomainEventPublisher.instance().publish(
                        new ClientsBatchDeleted(
                                allClientsOfQuery.stream().map(Client::getClientId).collect(Collectors.toSet())
                        )
                );
            } else {
                throw new RootClientDeleteException();
            }
            change.setDeletedIds(allClientsOfQuery.stream().map(e -> e.getClientId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            return allClientsOfQuery.stream().map(Client::getClientId).collect(Collectors.toSet());
        }, Client.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            ClientId clientId = new ClientId(id);
            Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
            if (client.isPresent()) {
                Client original = client.get();
                ClientPatchCommand beforePatch = new ClientPatchCommand(original);
                ClientPatchCommand afterPatch = DomainRegistry.customObjectSerializer().applyJsonPatch(command, beforePatch, ClientPatchCommand.class);
                RefreshTokenGrant refreshTokenGrantDetail = original.getPasswordGrant().getRefreshTokenGrant();
                original.replace(
                        afterPatch.getName(),
                        afterPatch.getDescription(),
                        afterPatch.isResourceIndicator(),
                        afterPatch.getScopeEnums(),
                        afterPatch.getGrantedAuthorities(),
                        afterPatch.getResourceIds() != null ? afterPatch.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.EMPTY_SET,
                        new ClientCredentialsGrant(afterPatch.getGrantTypeEnums(), afterPatch.getAccessTokenValiditySeconds()),
                        new PasswordGrant(afterPatch.getGrantTypeEnums(), afterPatch.getAccessTokenValiditySeconds(), refreshTokenGrantDetail)
                );
            }
        }, Client.class);
    }

    @Override
    public ClientDetails loadClientByClientId(String id) throws ClientRegistrationException {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        return client.map(ClientSpringOAuth2Representation::new).orElse(null);
    }

    @Transactional
    public void revokeTokenBasedOnChange(StoredEvent o) {
        if (EVENTS.contains(o.getName())) {
            DomainEvent deserialize = DomainRegistry.customObjectSerializer().deserialize(o.getEventBody(), DomainEvent.class);
            DomainRegistry.revokeTokenService().revokeClientToken((deserialize).getDomainId());
        }
    }
}
