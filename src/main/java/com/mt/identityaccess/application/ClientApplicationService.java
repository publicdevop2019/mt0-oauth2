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
import com.mt.identityaccess.domain.BatchClientRemoved;
import com.mt.identityaccess.domain.ClientRemoved;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientApplicationService implements ClientDetailsService {
    @Autowired
    private ObjectMapper om;
    @Transactional
    public ClientId provisionClient(ProvisionClientCommand command, String changeId) {
        return DomainRegistry.clientProvisioningService().provisionClient(
                new BasicClientDetail(
                        command.getName(),
                        command.getClientSecret(),
                        command.getDescription(),
                        command.getScopeEnums(),
                        command.getGrantedAuthorities(),
                        command.getResourceIds()
                ),
                new ClientCredentialsGrantDetail(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds()),
                new PasswordGrantDetail(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds()),
                new RefreshTokenGrantDetail(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds()),
                new AuthorizationCodeGrantDetail(command.getGrantTypeEnums(), command.getRegisteredRedirectUri())
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
                            command.getResourceIds()
                    ),
                    new ClientCredentialsGrantDetail(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds()),
                    new PasswordGrantDetail(command.getGrantTypeEnums(), command.getAccessTokenValiditySeconds()),
                    new RefreshTokenGrantDetail(command.getGrantTypeEnums(), command.getRefreshTokenValiditySeconds()),
                    new AuthorizationCodeGrantDetail(command.getGrantTypeEnums(), command.getRegisteredRedirectUri())
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
            if (client1.allowRemoval()) {
                DomainRegistry.clientRepository().remove(client1);
                DomainEventPublisher.instance().publish(new ClientRemoved(clientId));
            }
        }
    }

    @Transactional
    public void removeClients(String queryParam, String changeId) {
        List<Client> allClientsOfQuery = getAllClientsOfQuery(queryParam);
        boolean b = allClientsOfQuery.stream().anyMatch(e -> !e.allowRemoval());
        if (!b) {
            DomainRegistry.clientRepository().remove(allClientsOfQuery);
            DomainEventPublisher.instance().publish(
                    new BatchClientRemoved(
                            allClientsOfQuery.stream().map(Client::clientId).collect(Collectors.toSet())
                    )
            );
        }
    }

    @Transactional
    public void patchClient(String id, JsonPatch command, String changeId) {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        if(client.isPresent()){
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
                            middleLayer.getResourceIds()
                    ),
                    new ClientCredentialsGrantDetail(middleLayer.getGrantTypeEnums(), middleLayer.getAccessTokenValiditySeconds()),
                    new PasswordGrantDetail(middleLayer.getGrantTypeEnums(), middleLayer.getAccessTokenValiditySeconds())
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDetails loadClientByClientId(String id) throws ClientRegistrationException {
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(new ClientId(id));
        return client.map(ClientDetailsRepresentation::new).orElse(null);
    }

    private List<Client> getAllClientsOfQuery(String queryParam) {
        ClientQueryParam clientQueryParam = new ClientQueryParam(queryParam);
        QueryPagingParam queryPagingParam = new QueryPagingParam();
        SumPagedRep<Client> tSumPagedRep = DomainRegistry.clientRepository().clientsOfQuery(clientQueryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new ArrayList<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        List<Client> data = new ArrayList<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.clientRepository().clientsOfQuery(clientQueryParam, queryPagingParam.nextPage()).getData());
        }
        return data;
    }
}
