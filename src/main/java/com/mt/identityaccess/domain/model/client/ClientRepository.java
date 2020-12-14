package com.mt.identityaccess.domain.model.client;

import com.hw.shared.sql.SumPagedRep;
import com.mt.identityaccess.application.client.ClientPaging;
import com.mt.identityaccess.application.client.ClientQuery;
import com.mt.identityaccess.application.client.QueryConfig;

import java.util.Collection;
import java.util.Optional;

public interface ClientRepository {
    ClientId nextIdentity();

    Optional<Client> clientOfId(ClientId clientId);

    void add(Client client);

    void remove(Client client);

    SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging, QueryConfig queryConfig);

    SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging);

    void remove(Collection<Client> clients);
}
