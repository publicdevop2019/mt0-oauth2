package com.mt.identityaccess.domain.model.endpoint;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.endpoint.EndpointQuery;

import java.util.Collection;
import java.util.Optional;

public interface EndpointRepository {
    EndpointId nextIdentity();

    Optional<Endpoint> endpointOfId(EndpointId endpointId);

    void add(Endpoint endpoint);

    void remove(Endpoint endpoint);

    SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery, PageConfig endpointPaging, QueryConfig queryConfig);

    SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery, PageConfig endpointPaging);

    void remove(Collection<Endpoint> endpoints);
}
