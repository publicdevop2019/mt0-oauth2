package com.mt.identityaccess.domain.model.client;

import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.EndpointPaging;
import com.mt.identityaccess.application.client.EndpointQuery;
import com.mt.identityaccess.application.client.QueryConfig;

import java.util.Collection;
import java.util.Optional;

public interface EndpointRepository {
    EndpointId nextIdentity();

    Optional<Endpoint> endpointOfId(EndpointId endpointId);

    void add(Endpoint endpoint);

    void remove(Endpoint endpoint);

    SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery, EndpointPaging endpointPaging, QueryConfig queryConfig);

    SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery, EndpointPaging endpointPaging);

    void remove(Collection<Endpoint> endpoints);
}
