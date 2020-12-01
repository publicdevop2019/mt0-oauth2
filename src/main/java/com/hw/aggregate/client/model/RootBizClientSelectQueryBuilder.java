package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldBooleanEqualClause;
import com.hw.shared.sql.clause.SelectFieldNumberRangeClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.client.model.BizClient.*;

@Component
public class RootBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    {
        DEFAULT_PAGE_SIZE = 1000;
        MAX_PAGE_SIZE = 2000;
        mappedSortBy.put(ENTITY_NAME, ENTITY_NAME);
        mappedSortBy.put(ENTITY_RESOURCE_INDICATOR, ENTITY_RESOURCE_INDICATOR);
        mappedSortBy.put(ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS, ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS);
        supportedWhereField.put(ENTITY_RESOURCE_INDICATOR, new SelectFieldBooleanEqualClause<>(ENTITY_RESOURCE_INDICATOR));
        supportedWhereField.put(ENTITY_NAME, new SelectFieldStringLikeClause<>(ENTITY_NAME));
        supportedWhereField.put(ENTITY_GRANT_TYPE_ENUMS, new SelectFieldStringLikeClause<>(ENTITY_GRANT_TYPE_ENUMS));
        supportedWhereField.put(ENTITY_GRANT_AUTHORITIES, new SelectFieldStringLikeClause<>(ENTITY_GRANT_AUTHORITIES));
        supportedWhereField.put(ENTITY_SCOPE_ENUMS, new SelectFieldStringLikeClause<>(ENTITY_SCOPE_ENUMS));
        supportedWhereField.put(ENTITY_RESOURCE_IDS, new SelectFieldStringLikeClause<>(ENTITY_RESOURCE_IDS));
        supportedWhereField.put(ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS, new SelectFieldNumberRangeClause<>(ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS));
        allowEmptyClause = true;
    }
}
