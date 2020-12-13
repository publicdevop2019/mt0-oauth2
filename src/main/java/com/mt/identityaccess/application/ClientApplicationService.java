package com.mt.identityaccess.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.config.DomainEventPublisher;
import com.hw.shared.rest.exception.AggregatePatchException;
import com.hw.shared.sql.SumPagedRep;
import com.mt.identityaccess.application.command.ProvisionClientCommand;
import com.mt.identityaccess.application.command.ReplaceClientCommand;
import com.mt.identityaccess.application.representation.ClientDetailsRepresentation;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.*;
import com.mt.identityaccess.domain.model.client.event.ClientRemoved;
import com.mt.identityaccess.domain.model.client.event.ClientsBatchRemoved;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientApplicationService implements ClientDetailsService {
    @Autowired
    private ObjectMapper om;

    @Transactional
    public ClientId provisionClient(ProvisionClientCommand command, String changeId) {
        return DomainRegistry.clientService().provisionClient(
                new BasicClientDetail(
                        command.getName(),
                        command.getClientSecret(),
                        command.getDescription(),
                        command.getScopeEnums(),
                        command.getGrantedAuthorities(),
                        command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()),
                        command.isResourceIndicator()
                ),
                new ClientCredentialsGrantDetail(command.getGrantTypeEnums()),
                new PasswordGrantDetail(command.getGrantTypeEnums()),
                new RefreshTokenGrantDetail(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds()),
                new AuthorizationCodeGrantDetail(
                        command.getGrantTypeEnums(),
                        command.getRegisteredRedirectUri(),
                        command.isAutoApprove()
                ),
                new AccessTokenDetail(command.getAccessTokenValiditySeconds())
        );
    }

    @Transactional(readOnly = true)
    public SumPagedRep<Client> clients(String queryParam, String pagingParam, String configParam) {
        return DomainRegistry.clientRepository().clientsOfQuery(new ClientQueryParam(queryParam), new QueryPagingParam(pagingParam), new QueryConfigParam(configParam));
    }

    @Transactional(readOnly = true)
    public Optional<Client> client(String id) {
        return DomainRegistry.clientRepository().clientOfId(new ClientId(id));
    }

    @Transactional
    public void replaceClient(String id, ReplaceClientCommand command, String changeId) {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        if (client.isPresent()) {
            Client client1 = client.get();
            client1.replace(new BasicClientDetail(
                            command.getName(),
                            command.getClientSecret(),
                            command.getDescription(),
                            command.getScopeEnums(),
                            command.getGrantedAuthorities(),
                            command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()),
                            command.isResourceIndicator()
                    ),
                    new ClientCredentialsGrantDetail(command.getGrantTypeEnums()),
                    new PasswordGrantDetail(command.getGrantTypeEnums()),
                    new RefreshTokenGrantDetail(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds()),
                    new AuthorizationCodeGrantDetail(
                            command.getGrantTypeEnums(),
                            command.getRegisteredRedirectUri(),
                            command.isAutoApprove()
                    ),
                    new AccessTokenDetail(command.getAccessTokenValiditySeconds())
            );
            DomainRegistry.clientRepository().save(client1);
        }
    }

    @Transactional
    public void removeClient(String id, String changeId) {
        ClientId clientId = new ClientId(id);
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isPresent()) {
            Client client1 = client.get();
            if (client1.basicClientDetail().nonRoot()) {
                DomainRegistry.clientRepository().remove(client1);
                DomainEventPublisher.instance().publish(new ClientRemoved(clientId));
            }
        }
    }

    @Transactional
    public void removeClients(String queryParam, String changeId) {
        List<Client> allClientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQueryParam(queryParam));
        boolean b = allClientsOfQuery.stream().anyMatch(e -> !e.basicClientDetail().nonRoot());
        if (!b) {
            DomainRegistry.clientRepository().remove(allClientsOfQuery);
            DomainEventPublisher.instance().publish(
                    new ClientsBatchRemoved(
                            allClientsOfQuery.stream().map(Client::clientId).collect(Collectors.toSet())
                    )
            );
        }
    }

    @Transactional
    public void patchClient(String id, JsonPatch command, String changeId) {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        if (client.isPresent()) {
            Client original = client.get();
            ClientPatchingMiddleLayer middleLayer = new ClientPatchingMiddleLayer(original);
            try {
                JsonNode jsonNode = om.convertValue(middleLayer, JsonNode.class);
                JsonNode patchedNode = command.apply(jsonNode);
                middleLayer = om.treeToValue(patchedNode, middleLayer.getClazz());
            } catch (JsonPatchException | JsonProcessingException e) {
                throw new AggregatePatchException();
            }
            original.replace(new BasicClientDetail(
                            middleLayer.getName(),
                            middleLayer.getDescription(),
                            middleLayer.getScopeEnums(),
                            middleLayer.getGrantedAuthorities(),
                            middleLayer.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()),
                            middleLayer.isResourceIndicator()
                    ),
                    new ClientCredentialsGrantDetail(middleLayer.getGrantTypeEnums()),
                    new PasswordGrantDetail(middleLayer.getGrantTypeEnums()),
                    new AccessTokenDetail(middleLayer.getAccessTokenValiditySeconds())
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDetails loadClientByClientId(String id) throws ClientRegistrationException {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        return client.map(ClientDetailsRepresentation::new).orElse(null);
    }
}
