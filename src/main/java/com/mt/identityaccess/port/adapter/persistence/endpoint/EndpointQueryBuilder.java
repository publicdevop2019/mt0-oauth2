package com.mt.identityaccess.port.adapter.persistence.endpoint;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.port.adapter.persistence.client.SelectFieldDomainIdEqualClause;
import com.mt.identityaccess.port.adapter.persistence.client.SelectFieldIsNullClause;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;
import static com.mt.identityaccess.domain.model.endpoint.Endpoint.*;

@Component
public class EndpointQueryBuilder extends SelectQueryBuilder<Endpoint> {
    {
        DEFAULT_PAGE_SIZE = 10;
        MAX_PAGE_SIZE = 40;
        allowEmptyClause = true;
        mappedSortBy.put("resourceId", "clientId");
        mappedSortBy.put("path", ENTITY_PATH);
        mappedSortBy.put("method", ENTITY_METHOD);
        supportedWhereField.put(COMMON_ENTITY_ID, new SelectFieldDomainIdEqualClause<>("endpointId"));
        supportedWhereField.put("resourceId", new SelectFieldDomainIdEqualClause<>("clientId"));
        supportedWhereField.put("path", new SelectFieldStringEqualClause<>(ENTITY_PATH));
        supportedWhereField.put(ENTITY_EXPRESSION, new SelectFieldIsNullClause<>(ENTITY_EXPRESSION));
        supportedWhereField.put(ENTITY_METHOD, new SelectFieldStringEqualClause<>(ENTITY_METHOD));
    }

}
