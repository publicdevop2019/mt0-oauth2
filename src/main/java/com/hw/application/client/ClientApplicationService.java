package com.hw.application.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.application.ApplicationServiceRegistry;
import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.DomainRegistry;
import com.hw.domain.model.client.*;
import com.hw.domain.model.client.event.ClientRemoved;
import com.hw.domain.model.client.event.ClientsBatchRemoved;
import com.hw.shared.rest.exception.AggregatePatchException;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ClientApplicationService implements ClientDetailsService {
    @Autowired
    private ObjectMapper om;

    @Transactional
    public ClientId provisionClient(ProvisionClientCommand command, String operationId) {
        return ApplicationServiceRegistry.clientIdempotentApplicationService().idempotentProvision(command, operationId,
                () -> {
                    ClientId clientId = DomainRegistry.clientRepository().nextIdentity();
                    return DomainRegistry.clientService().provisionClient(
                            clientId,
                            command.getName(),
                            command.getClientSecret(),
                            command.getDescription(),
                            command.isResourceIndicator(),
                            command.getScopeEnums(),
                            command.getGrantedAuthorities(),
                            command.getResourceIds() != null ? command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.EMPTY_SET,
                            new ClientCredentialsGrantDetail(command.getGrantTypeEnums(), clientId),
                            new PasswordGrantDetail(command.getGrantTypeEnums(), clientId),
                            new RefreshTokenGrantDetail(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds(), clientId),
                            new AuthorizationCodeGrantDetail(
                                    command.getGrantTypeEnums(),
                                    command.getRegisteredRedirectUri(),
                                    command.isAutoApprove(),
                                    clientId
                            ),
                            new AccessTokenDetail(command.getAccessTokenValiditySeconds(), clientId)
                    );
                }
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

    @Transactional
    public void replaceClient(String id, ReplaceClientCommand command, String changeId) {
        ClientId clientId = new ClientId(id);
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isPresent()) {
            Client client1 = client.get();
            ApplicationServiceRegistry.clientIdempotentApplicationService().idempotent(command, changeId, (ignored) -> {
                client1.replace(
                        command.getName(),
                        command.getClientSecret(),
                        command.getDescription(),
                        command.isResourceIndicator(),
                        command.getScopeEnums(),
                        command.getGrantedAuthorities(),
                        command.getResourceIds() != null ? command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.EMPTY_SET,
                        new ClientCredentialsGrantDetail(command.getGrantTypeEnums(), clientId),
                        new PasswordGrantDetail(command.getGrantTypeEnums(), clientId),
                        new RefreshTokenGrantDetail(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds(),clientId),
                        new AuthorizationCodeGrantDetail(
                                command.getGrantTypeEnums(),
                                command.getRegisteredRedirectUri(),
                                command.isAutoApprove(),
                                clientId
                        ),
                        new AccessTokenDetail(command.getAccessTokenValiditySeconds())
                );
            });
            DomainRegistry.clientRepository().add(client1);
        }
    }

    @Transactional
    public void removeClient(String id, String changeId) {
        ClientId clientId = new ClientId(id);
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isPresent()) {
            Client client1 = client.get();
            if (client1.nonRoot()) {
                ApplicationServiceRegistry.clientIdempotentApplicationService().idempotent(null, changeId, (ignored) -> {
                    DomainRegistry.clientRepository().remove(client1);
                });
                DomainEventPublisher.instance().publish(new ClientRemoved(clientId));
            } else {
                throw new RootClientDeleteException();
            }
        }
    }

    @Transactional
    public void removeClients(String queryParam, String changeId) {
        List<Client> allClientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(queryParam));
        boolean b = allClientsOfQuery.stream().anyMatch(e -> !e.nonRoot());
        if (!b) {
            ApplicationServiceRegistry.clientIdempotentApplicationService().idempotent(null, changeId, (ignored) -> {
                DomainRegistry.clientRepository().remove(allClientsOfQuery);
            });
            DomainEventPublisher.instance().publish(
                    new ClientsBatchRemoved(
                            allClientsOfQuery.stream().map(Client::clientId).collect(Collectors.toSet())
                    )
            );
        } else {
            throw new RootClientDeleteException();
        }
    }

    @Transactional
    public void patchClient(String id, JsonPatch command, String changeId) {
        ClientId clientId = new ClientId(id);
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isPresent()) {
            Client original = client.get();
            ClientPatchingCommand middleLayer = new ClientPatchingCommand(original);
            try {
                JsonNode jsonNode = om.convertValue(middleLayer, JsonNode.class);
                JsonNode patchedNode = command.apply(jsonNode);
                middleLayer = om.treeToValue(patchedNode, ClientPatchingCommand.class);
            } catch (JsonPatchException | JsonProcessingException e) {
                throw new AggregatePatchException();
            }
            ClientPatchingCommand finalMiddleLayer = middleLayer;
            ApplicationServiceRegistry.clientIdempotentApplicationService().idempotent(command, changeId, (ignored) -> {
                original.replace(
                        finalMiddleLayer.getName(),
                        finalMiddleLayer.getDescription(),
                        finalMiddleLayer.isResourceIndicator(),
                        finalMiddleLayer.getScopeEnums(),
                        finalMiddleLayer.getGrantedAuthorities(),
                        finalMiddleLayer.getResourceIds() != null ? finalMiddleLayer.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.EMPTY_SET,
                        new ClientCredentialsGrantDetail(finalMiddleLayer.getGrantTypeEnums(), clientId),
                        new PasswordGrantDetail(finalMiddleLayer.getGrantTypeEnums(), clientId),
                        new AccessTokenDetail(finalMiddleLayer.getAccessTokenValiditySeconds())
                );
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDetails loadClientByClientId(String id) throws ClientRegistrationException {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        return client.map(ClientDetailsRepresentation::new).orElse(null);
    }
}
