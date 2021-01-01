package com.mt.identityaccess.port.adapter.persistence.revoke_token;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldStringEqualClause;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import org.springframework.stereotype.Component;

import static com.mt.identityaccess.domain.model.revoke_token.RevokeToken.ENTITY_ISSUE_AT;
import static com.mt.identityaccess.domain.model.revoke_token.RevokeToken.ENTITY_TARGET_ID;

@Component
public class RevokeTokenQueryBuilder extends SelectQueryBuilder<RevokeToken> {
    {
        mappedSortBy.put(ENTITY_ISSUE_AT, ENTITY_ISSUE_AT);
        supportedWhereField.put("targetId", new SelectFieldStringEqualClause<>(ENTITY_TARGET_ID));
        allowEmptyClause = true;
    }

}
