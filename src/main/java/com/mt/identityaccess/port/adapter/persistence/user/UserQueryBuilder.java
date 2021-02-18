package com.mt.identityaccess.port.adapter.persistence.user;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.DomainIdQueryClause;
import com.mt.common.sql.clause.SelectFieldBooleanEqualClause;
import com.mt.common.sql.clause.SelectFieldStringLikeClause;
import com.mt.identityaccess.domain.model.user.User;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;
import static com.mt.identityaccess.domain.model.user.User.*;

@Component
public class UserQueryBuilder extends SelectQueryBuilder<User> {
    private static final String USER_ID_LITERAL = "userId";

    {
        supportedSort.put(ENTITY_EMAIL, ENTITY_EMAIL);
        supportedSort.put("createdAt", "createdAt");
        supportedWhere.put(ENTITY_EMAIL, new SelectFieldUserEmailClause());
        supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(USER_ID_LITERAL));
        supportedWhere.put(ENTITY_SUBSCRIPTION, new SelectFieldBooleanEqualClause<>(ENTITY_SUBSCRIPTION));
        supportedWhere.put(ENTITY_GRANTED_AUTHORITIES, new SelectFieldStringLikeClause<>(ENTITY_GRANTED_AUTHORITIES));
    }

}