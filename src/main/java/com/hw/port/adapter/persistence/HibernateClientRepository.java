package com.hw.port.adapter.persistence;

import com.hw.application.client.ClientPaging;
import com.hw.application.client.ClientQuery;
import com.hw.application.client.QueryConfig;
import com.hw.domain.model.client.Client;
import com.hw.domain.model.client.ClientId;
import com.hw.domain.model.client.ClientRepository;
import com.hw.shared.sql.SumPagedRep;
import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HibernateClientRepository extends JpaRepository<Client, Long>, ClientRepository {
    Optional<Client> findByClientId(ClientId clientId);

    default ClientId nextIdentity() {
        return new ClientId("CLIENT-" + UUID.randomUUID().toString().replace("-", ""));
    }

    default Optional<Client> clientOfId(ClientId clientId) {
        return findByClientId(clientId);
    }

    default void add(Client client) {
        save(client);
    }

    default void remove(Client client) {
        delete(client);
    }

    default void remove(Collection<Client> client) {
        deleteAll(client);
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging, QueryConfig queryConfig) {
        return getSumPagedRep(clientQuery.value, clientPaging.value, queryConfig.value);
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging) {
        return getSumPagedRep(clientQuery.value, clientPaging.value, null);
    }

    private SumPagedRep<Client> getSumPagedRep(String query, String page, String config) {
        SelectQueryBuilder<Client> selectQueryBuilder = QueryBuilderRegistry.clientSelectQueryBuilder();
        List<Client> select = selectQueryBuilder.select(query, page, Client.class);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = selectQueryBuilder.selectCount(query, Client.class);
        }
        return new SumPagedRep<>(select, aLong);
    }

    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }
}
