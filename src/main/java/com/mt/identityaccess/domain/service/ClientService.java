package com.mt.identityaccess.domain.service;

import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.ClientQuery;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.*;
import com.mt.identityaccess.domain.model.client.event.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ClientService {

    public ClientId create(ClientId clientId,
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
                authorizationCodeGrant
        );
        DomainRegistry.clientRepository().add(client);
        DomainEventPublisher.instance().publish(new ClientCreated(client.getClientId()));
        return clientId;
    }

    public List<Client> getClientsOfQuery(ClientQuery queryParam) {
        DefaultPaging queryPagingParam = new DefaultPaging();
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
