package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.identityaccess.application.client.*;
import com.mt.identityaccess.domain.model.client.Endpoint;
import com.mt.identityaccess.domain.model.client.EndpointId;
import com.mt.identityaccess.domain.model.client.EndpointRepository;
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
public interface SpringDataJpaEndpointRepository extends JpaRepository<Endpoint, Long>, EndpointRepository {
    Optional<Endpoint> findByEndpointIdAndDeletedFalse(EndpointId endpointId);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default EndpointId nextIdentity() {
        return new EndpointId();
    }

    default Optional<Endpoint> endpointOfId(EndpointId endpointId) {
        return findByEndpointIdAndDeletedFalse(endpointId);
    }

    default void add(Endpoint endpoint) {
        save(endpoint);
    }

    default void remove(Endpoint endpoint) {
        softDelete(endpoint.getId());
    }

    default void remove(Collection<Endpoint> endpoints) {
        softDeleteAll(endpoints.stream().map(Endpoint::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery, EndpointPaging endpointPaging, QueryConfig queryConfig) {
        return getSumPagedRep(endpointQuery.value(), endpointPaging.value(), queryConfig.value());
    }

    default SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery clientQuery, EndpointPaging clientPaging) {
        return getSumPagedRep(clientQuery.value(), clientPaging.value(), null);
    }

    private SumPagedRep<Endpoint> getSumPagedRep(String query, String page, String config) {
        SelectQueryBuilder<Endpoint> selectQueryBuilder = QueryBuilderRegistry.endpointSelectQueryBuilder();
        List<Endpoint> select = selectQueryBuilder.select(query, page, Endpoint.class);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = selectQueryBuilder.selectCount(query, Endpoint.class);
        }
        return new SumPagedRep<>(select, aLong);
    }

    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }
}
