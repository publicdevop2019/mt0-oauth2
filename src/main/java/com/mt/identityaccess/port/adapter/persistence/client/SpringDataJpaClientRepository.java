package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.identityaccess.application.client.ClientPaging;
import com.mt.identityaccess.application.client.ClientQuery;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import com.mt.identityaccess.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaClientRepository extends JpaRepository<Client, Long>, ClientRepository {
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Client> findByClientIdAndDeletedFalse(ClientId clientId);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    @Modifying
    @Query(nativeQuery = true, value = "delete from resources_map e where e.domain_id= ?1")
    void removeClientFromResourcesMap(String domainId);

    default ClientId nextIdentity() {
        return new ClientId();
    }

    default Optional<Client> clientOfId(ClientId clientId) {
        return findByClientIdAndDeletedFalse(clientId);
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

    default void removeResourceClient(ClientId clientId) {
        removeClientFromResourcesMap(clientId.getDomainId());
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging, QueryConfig queryConfig) {
        return getSumPagedRep(clientQuery.value(), clientPaging.value(), queryConfig.value());
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging) {
        return getSumPagedRep(clientQuery.value(), clientPaging.value(), null);
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
