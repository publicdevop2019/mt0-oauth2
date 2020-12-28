package com.mt.identityaccess.port.adapter.persistence;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldBooleanEqualClause;
import com.mt.common.sql.clause.SelectFieldStringLikeClause;
import com.mt.identityaccess.domain.model.client.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientQueryBuilder extends SelectQueryBuilder<Client> {
    public static final String ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String ENTITY_RESOURCE_INDICATOR = "resourceIndicator";
    public static final String ENTITY_CLIENT_ID = "clientId";
    public static final String ENTITY_NAME = "name";
    public static final String ENTITY_GRANT_TYPE_ENUMS = "grantTypeEnums";
    public static final String ENTITY_GRANT_AUTHORITIES = "grantedAuthorities";
    public static final String ENTITY_SCOPE_ENUMS = "scopeEnums";
    public static final String ENTITY_RESOURCE_IDS = "resourceIds";

    {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 2000;
        sortConverter = new ClientOrderConverter();
        supportedWhereField.put(ENTITY_RESOURCE_INDICATOR, new SelectFieldBooleanEqualClause<>("accessible"));
        supportedWhereField.put(ENTITY_CLIENT_ID, new SelectFieldClientIdEqualClause());
        supportedWhereField.put(ENTITY_NAME, new SelectFieldStringLikeClause<>(ENTITY_NAME));
        supportedWhereField.put(ENTITY_GRANT_TYPE_ENUMS, new SelectFieldGrantEnabledBooleanClause());
        supportedWhereField.put(ENTITY_GRANT_AUTHORITIES, new SelectFieldStringLikeClause<>("authorities"));
        supportedWhereField.put(ENTITY_SCOPE_ENUMS, new SelectFieldStringLikeClause<>(ENTITY_SCOPE_ENUMS));
        supportedWhereField.put(ENTITY_RESOURCE_IDS, new SelectFieldResourceIdsClause());
        supportedWhereField.put(ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS, new SelectFieldGrantAccessTokenClause());
        allowEmptyClause = true;
    }
}
