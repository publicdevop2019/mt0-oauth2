package com.mt.identityaccess.domain.model.endpoint;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.SumPagedRep;

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
