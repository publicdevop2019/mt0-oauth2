package com.mt.identityaccess.domain.model.client;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.ClientQuery;

import java.util.Collection;
import java.util.Optional;

public interface ClientRepository {
    ClientId nextIdentity();

    Optional<Client> clientOfId(ClientId clientId);

    void add(Client client);

    void remove(Client client);

    SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, DefaultPaging clientPaging, QueryConfig queryConfig);

    SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, DefaultPaging clientPaging);

    void remove(Collection<Client> clients);

}
