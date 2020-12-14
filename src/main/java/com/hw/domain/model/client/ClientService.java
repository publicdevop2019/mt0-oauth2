package com.hw.domain.model.client;

import com.hw.application.client.ClientPaging;
import com.hw.application.client.ClientQuery;
import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.DomainRegistry;
import com.hw.domain.model.client.event.ClientProvisioned;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ClientService {

    public ClientId provisionClient(String name,
                                    String description,
                                    Set<Scope> scopes,
                                    Set<Authority> authorities,
                                    Set<ClientId> resources,
                                    boolean accessible,
                                    ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                                    PasswordGrantDetail passwordGrantDetail,
                                    RefreshTokenGrantDetail refreshTokenGrantDetail,
                                    AuthorizationCodeGrantDetail authorizationCodeGrantDetail,
                                    AccessTokenDetail accessTokenDetail
    ) {
        ClientId clientId = DomainRegistry.clientRepository().nextIdentity();
        Client client = new Client(clientId, name, description, scopes, authorities, resources, accessible,
                clientCredentialsGrantDetail, passwordGrantDetail, refreshTokenGrantDetail, authorizationCodeGrantDetail, accessTokenDetail);
        DomainRegistry.clientRepository().add(client);
        DomainEventPublisher.instance().publish(new ClientProvisioned(client.clientId()));
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
}
