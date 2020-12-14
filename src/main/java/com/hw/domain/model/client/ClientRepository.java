package com.hw.domain.model.client;

import com.hw.shared.sql.SumPagedRep;
import com.hw.application.client.ClientPaging;
import com.hw.application.client.ClientQuery;
import com.hw.application.client.QueryConfig;

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
