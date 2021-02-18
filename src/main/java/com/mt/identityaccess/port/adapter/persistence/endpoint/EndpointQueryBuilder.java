package com.mt.identityaccess.port.adapter.persistence.endpoint;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.port.adapter.persistence.client.SelectFieldDomainIdEqualClause;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;
import static com.mt.identityaccess.domain.model.endpoint.Endpoint.*;

@Component
public class EndpointQueryBuilder extends SelectQueryBuilder<Endpoint> {
    {
        supportedSort.put("resourceId", "clientId");
        supportedSort.put("path", ENTITY_PATH);
        supportedSort.put("method", ENTITY_METHOD);
        supportedWhere.put(COMMON_ENTITY_ID, new SelectFieldDomainIdEqualClause<>("endpointId"));
        supportedWhere.put("resourceId", new SelectFieldDomainIdEqualClause<>("clientId"));
        supportedWhere.put("path", new SelectFieldStringEqualClause<>(ENTITY_PATH));
        supportedWhere.put(ENTITY_METHOD, new SelectFieldStringEqualClause<>(ENTITY_METHOD));
    }

}
