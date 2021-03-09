package com.mt.identityaccess.port.adapter.persistence.endpoint;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.domain.model.endpoint.EndpointId;
import com.mt.identityaccess.domain.model.endpoint.EndpointQuery;
import com.mt.identityaccess.domain.model.endpoint.EndpointRepository;
import com.mt.identityaccess.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.identityaccess.domain.model.endpoint.Endpoint.ENTITY_METHOD;
import static com.mt.identityaccess.domain.model.endpoint.Endpoint.ENTITY_PATH;

@Repository
public interface SpringDataJpaEndpointRepository extends JpaRepository<Endpoint, Long>, EndpointRepository {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default Optional<Endpoint> endpointOfId(EndpointId endpointId) {
        return endpointsOfQuery(new EndpointQuery(endpointId)).findFirst();
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

    default SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery query) {
        return QueryBuilderRegistry.getEndpointQueryBuilder().execute(query);
    }


    @Component
    class JpaCriteriaApiEndpointAdapter {

        public static final String ENDPOINT_ID = "endpointId";
        public static final String CLIENT_ID = "clientId";

        public SumPagedRep<Endpoint> execute(EndpointQuery endpointQuery) {
            QueryUtility.QueryContext<Endpoint> queryContext = QueryUtility.prepareContext(Endpoint.class, endpointQuery);
            Optional.ofNullable(endpointQuery.getEndpointIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), ENDPOINT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getClientIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), CLIENT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getPath()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e, ENTITY_PATH, queryContext));
            Optional.ofNullable(endpointQuery.getMethod()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e, ENTITY_METHOD, queryContext));
            Order order = null;
            if (endpointQuery.getEndpointSort().isById())
                order = QueryUtility.getDomainIdOrder(ENDPOINT_ID, queryContext, endpointQuery.getEndpointSort().isAsc());
            if (endpointQuery.getEndpointSort().isByClientId())
                order = QueryUtility.getOrder(CLIENT_ID, queryContext, endpointQuery.getEndpointSort().isAsc());
            if (endpointQuery.getEndpointSort().isByPath())
                order = QueryUtility.getOrder(ENTITY_PATH, queryContext, endpointQuery.getEndpointSort().isAsc());
            if (endpointQuery.getEndpointSort().isByMethod())
                order = QueryUtility.getOrder(ENTITY_METHOD, queryContext, endpointQuery.getEndpointSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(endpointQuery, queryContext);
        }


    }
}
