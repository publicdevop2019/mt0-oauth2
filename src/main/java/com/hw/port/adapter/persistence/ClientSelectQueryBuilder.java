package com.hw.port.adapter.persistence;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldBooleanEqualClause;
import com.hw.shared.sql.clause.SelectFieldNumberRangeClause;
import com.hw.shared.sql.clause.SelectFieldStringLikeClause;
import com.hw.domain.model.client.Client;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
@Component
public class ClientSelectQueryBuilder extends SelectQueryBuilder<Client> {
    public static final String ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String ENTITY_RESOURCE_INDICATOR = "resourceIndicator";
    public static final String ENTITY_NAME = "name";
    public static final String ENTITY_GRANT_TYPE_ENUMS = "grantTypeEnums";
    public static final String ENTITY_GRANT_AUTHORITIES = "grantedAuthorities";
    public static final String ENTITY_SCOPE_ENUMS = "scopeEnums";
    public static final String ENTITY_RESOURCE_IDS = "resourceIds";

    {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 40;
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
