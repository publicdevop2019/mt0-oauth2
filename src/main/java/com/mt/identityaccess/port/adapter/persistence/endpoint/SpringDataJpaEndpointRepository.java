package com.mt.identityaccess.port.adapter.persistence.endpoint;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.identityaccess.application.endpoint.EndpointQuery;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.domain.model.endpoint.EndpointId;
import com.mt.identityaccess.domain.model.endpoint.EndpointRepository;
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
        return getEndpointOfId(endpointId);
    }

    private Optional<Endpoint> getEndpointOfId(EndpointId endpointId) {
        SelectQueryBuilder<Endpoint> endpointSelectQueryBuilder = QueryBuilderRegistry.endpointSelectQueryBuilder();
        List<Endpoint> select = endpointSelectQueryBuilder.select(new EndpointQuery(endpointId), new PageConfig(), Endpoint.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
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

    default SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery query, PageConfig endpointPaging, QueryConfig queryConfig) {
        return getSumPagedRep(query, endpointPaging, queryConfig);
    }

    default SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery query, PageConfig clientPaging) {
        return getSumPagedRep(query, clientPaging, new QueryConfig());
    }

    private SumPagedRep<Endpoint> getSumPagedRep(EndpointQuery query, PageConfig page, QueryConfig config) {
        SelectQueryBuilder<Endpoint> selectQueryBuilder = QueryBuilderRegistry.endpointSelectQueryBuilder();
        List<Endpoint> select = selectQueryBuilder.select(query, page, Endpoint.class);
        Long aLong = null;
        if (!config.isSkipCount()) {
            aLong = selectQueryBuilder.count(query, Endpoint.class);
        }
        return new SumPagedRep<>(select, aLong);
    }
}
