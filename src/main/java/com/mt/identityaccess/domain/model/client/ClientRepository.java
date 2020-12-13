package com.mt.identityaccess.domain.model.client;

import com.hw.shared.sql.SumPagedRep;

import java.util.Collection;
import java.util.Optional;

public interface ClientRepository {
    ClientId nextIdentity();

    Optional<Client> clientOfId(ClientId clientId);

    void save(Client client);

    void remove(Client client);

    SumPagedRep<Client> clientsOfQuery(ClientQueryParam clientQueryParam, QueryPagingParam clientPagingParam, QueryConfigParam queryConfigParam);

    SumPagedRep<Client> clientsOfQuery(ClientQueryParam clientQueryParam, QueryPagingParam clientPagingParam);

    void remove(Collection<Client> clients);
}
