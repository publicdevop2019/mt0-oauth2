package com.mt.identityaccess.domain.model.client;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.identityaccess.application.client.ClientQuery;

import java.util.Collection;
import java.util.Optional;

public interface ClientRepository {
    ClientId nextIdentity();

    Optional<Client> clientOfId(ClientId clientId);

    void add(Client client);

    void remove(Client client);

    SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, PageConfig clientPaging, QueryConfig queryConfig);

    SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, PageConfig clientPaging);

    void remove(Collection<Client> clients);

}
