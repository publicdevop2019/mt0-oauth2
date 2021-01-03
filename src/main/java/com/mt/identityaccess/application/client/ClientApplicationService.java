package com.mt.identityaccess.application.client;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.domain_event.DomainEvent;
import com.mt.common.domain_event.DomainEventPublisher;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientApplicationService implements ClientDetailsService {

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
                },Client.class
        );

    }

    @Transactional(readOnly = true)
    public SumPagedRep<Client> clients(String queryParam, String pagingParam, String configParam) {
        return DomainRegistry.clientRepository().clientsOfQuery(new ClientQuery(queryParam), new ClientPaging(pagingParam), new QueryConfig(configParam));
    }

    @Transactional(readOnly = true)
    public Optional<Client> client(String id) {
        return DomainRegistry.clientRepository().clientOfId(new ClientId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replaceClient(String id, ClientUpdateCommand command, String changeId) {
        ClientId clientId = new ClientId(id);
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isPresent()) {
            Client client1 = client.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
                RefreshTokenGrant refreshTokenGrantDetail = new RefreshTokenGrant(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds());
                client1.replace(
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
            },Client.class);
            DomainRegistry.clientRepository().add(client1);
        }
    }

    @SubscribeForEvent
    @Transactional
    public void removeClient(String id, String changeId) {
        ClientId clientId = new ClientId(id);
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isPresent()) {
            Client client1 = client.get();
            if (client1.isNonRoot()) {
                ApplicationServiceRegistry.idempotentWrapper().idempotent(null, changeId, (ignored) -> {
                    DomainRegistry.clientRepository().remove(client1);
                },Client.class);
                DomainEventPublisher.instance().publish(new ClientDeleted(clientId));
            } else {
                throw new RootClientDeleteException();
            }
        }
    }

    @SubscribeForEvent
    @Transactional
    public void removeClients(String queryParam, String changeId) {
        List<Client> allClientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(queryParam));
        boolean b = allClientsOfQuery.stream().anyMatch(e -> !e.isNonRoot());
        if (!b) {
            ApplicationServiceRegistry.idempotentWrapper().idempotent(null, changeId, (ignored) -> {
                DomainRegistry.clientRepository().remove(allClientsOfQuery);
            },Client.class);
            DomainEventPublisher.instance().publish(
                    new ClientsBatchDeleted(
                            allClientsOfQuery.stream().map(Client::getClientId).collect(Collectors.toSet())
                    )
            );
        } else {
            throw new RootClientDeleteException();
        }
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ClientId clientId = new ClientId(id);
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isPresent()) {
            Client original = client.get();
            ClientPatchCommand beforePatch = new ClientPatchCommand(original);
            ClientPatchCommand afterPatch = DomainRegistry.customObjectSerializer().applyJsonPatch(command, beforePatch, ClientPatchCommand.class);
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
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
            },Client.class);
        }
    }

    @Override
    public ClientDetails loadClientByClientId(String id) throws ClientRegistrationException {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        return client.map(ClientSpringOAuth2Representation::new).orElse(null);
    }

    public void revokeTokenBasedOnChange(Object o) {
        if (
                o instanceof ClientAccessibleChanged ||
                        o instanceof ClientAccessTokenValiditySecondsChanged ||
                        o instanceof ClientAuthoritiesChanged ||
                        o instanceof ClientGrantTypeChanged ||
                        o instanceof ClientRefreshTokenChanged ||
                        o instanceof ClientDeleted ||
                        o instanceof ClientResourcesChanged ||
                        o instanceof ClientScopesChanged ||
                        o instanceof ClientSecretChanged
        ) {
            DomainRegistry.revokeTokenService().revokeClientToken(((DomainEvent) o).getDomainId());
        }

    }
}
