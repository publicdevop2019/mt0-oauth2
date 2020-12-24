package com.mt.identityaccess.domain.model.client;

import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.ClientPaging;
import com.mt.identityaccess.application.client.ClientQuery;
import com.mt.common.domain.model.DomainEvent;
import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.event.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ClientService {

    public ClientId provisionClient(ClientId clientId,
                                    String name,
                                    String clientSecret,
                                    String description,
                                    boolean accessible,
                                    Set<Scope> scopes,
                                    Set<Authority> authorities,
                                    Set<ClientId> resources,
                                    ClientCredentialsGrant clientCredentialsGrant,
                                    PasswordGrant passwordGrant,
                                    AuthorizationCodeGrant authorizationCodeGrant
    ) {

        Client client = new Client(
                clientId,
                name,
                clientSecret,
                description,
                accessible,
                scopes,
                authorities,
                resources,
                clientCredentialsGrant,
                passwordGrant,
                authorizationCodeGrant,
                DomainRegistry.uniqueIdGeneratorService()
        );
        DomainRegistry.clientRepository().add(client);
        DomainEventPublisher.instance().publish(new ClientProvisioned(client.getClientId()));
        return clientId;
    }

    public List<Client> getClientsOfQuery(ClientQuery queryParam) {
        ClientPaging queryPagingParam = new ClientPaging();
        SumPagedRep<Client> tSumPagedRep = DomainRegistry.clientRepository().clientsOfQuery(queryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new ArrayList<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        List<Client> data = new ArrayList<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.clientRepository().clientsOfQuery(queryParam, queryPagingParam.nextPage()).getData());
        }
        return data;
    }

    public void revokeTokenBasedOnChange(Object o) {
        if (
                o instanceof ClientAccessibleChanged ||
                        o instanceof ClientAccessTokenValiditySecondsChanged ||
                        o instanceof ClientAuthoritiesChanged ||
                        o instanceof ClientGrantTypeChanged ||
                        o instanceof ClientRefreshTokenChanged ||
                        o instanceof ClientRemoved ||
                        o instanceof ClientResourcesChanged ||
                        o instanceof ClientScopesChanged ||
                        o instanceof ClientSecretChanged
        ) {
            DomainRegistry.revokeTokenService().revokeClientToken(((DomainEvent) o).getClientId());
        }

    }
}
