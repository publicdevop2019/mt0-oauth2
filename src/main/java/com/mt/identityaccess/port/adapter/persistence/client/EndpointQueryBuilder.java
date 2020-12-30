package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.identityaccess.domain.model.client.Endpoint;
import org.springframework.stereotype.Component;

import static com.mt.identityaccess.domain.model.client.Endpoint.*;

@Component
public class EndpointQueryBuilder extends SelectQueryBuilder<Endpoint> {
    {
        DEFAULT_PAGE_SIZE = 10;
        MAX_PAGE_SIZE = 20;
        allowEmptyClause = true;
        mappedSortBy.put("resourceId", ENTITY_RESOURCE_ID);
        mappedSortBy.put("path", ENTITY_PATH);
        mappedSortBy.put("method", ENTITY_METHOD);
        supportedWhereField.put(ENTITY_RESOURCE_ID, new SelectFieldStringEqualClause<>(ENTITY_RESOURCE_ID));
        supportedWhereField.put("path", new SelectFieldStringEqualClause<>(ENTITY_PATH));
        supportedWhereField.put(ENTITY_EXPRESSION, new SelectFieldIsNullClause<>(ENTITY_EXPRESSION));
        supportedWhereField.put(ENTITY_METHOD, new SelectFieldStringEqualClause<>(ENTITY_METHOD));
    }

}
