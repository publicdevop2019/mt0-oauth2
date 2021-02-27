package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.identityaccess.domain.model.client.ClientQuery;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import com.mt.identityaccess.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaClientRepository extends JpaRepository<Client, Long>, ClientRepository {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default ClientId nextIdentity() {
        return new ClientId();
    }

    default Optional<Client> clientOfId(ClientId clientId) {
        return getClientOfId(clientId);
    }

    private Optional<Client> getClientOfId(ClientId clientId) {
        SelectQueryBuilder<Client> clientSelectQueryBuilder = QueryBuilderRegistry.clientSelectQueryBuilder();
        List<Client> select = clientSelectQueryBuilder.select(new ClientQuery(clientId), new PageConfig(), Client.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
    }

    default void add(Client client) {
        save(client);
    }

    default void remove(Client client) {
        softDelete(client.getId());
    }

    default void remove(Collection<Client> client) {
        softDeleteAll(client.stream().map(Client::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, PageConfig clientPaging, QueryConfig queryConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.clientSelectQueryBuilder(), clientQuery, clientPaging, queryConfig, Client.class);
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, PageConfig clientPaging) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.clientSelectQueryBuilder(), clientQuery, clientPaging, new QueryConfig(), Client.class);
    }
}
