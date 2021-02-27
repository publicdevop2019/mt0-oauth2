package com.mt.identityaccess.domain.model.endpoint;


import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.identityaccess.domain.model.endpoint.EndpointId;

public class EndpointQuery extends QueryCriteria {

    public EndpointQuery(String queryParam) {
        super(queryParam);
    }

    public EndpointQuery(EndpointId endpointId) {
        super(endpointId);
    }
}
