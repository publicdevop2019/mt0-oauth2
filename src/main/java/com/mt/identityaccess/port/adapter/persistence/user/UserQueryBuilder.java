package com.mt.identityaccess.port.adapter.persistence.user;

import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.common.domain.model.sql.clause.DomainIdQueryClause;
import com.mt.common.domain.model.sql.clause.FieldBooleanEqualClause;
import com.mt.common.domain.model.sql.clause.FieldStringLikeClause;
import com.mt.identityaccess.domain.model.user.User;
import org.springframework.stereotype.Component;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;
import static com.mt.identityaccess.domain.model.user.User.*;

@Component
public class UserQueryBuilder extends SelectQueryBuilder<User> {
    private static final String USER_ID_LITERAL = "userId";

    {
        supportedSort.put("id", USER_ID_LITERAL);
        supportedSort.put(ENTITY_EMAIL, ENTITY_EMAIL);
        supportedSort.put("createdAt", "createdAt");
        supportedSort.put("locked", ENTITY_LOCKED);
        supportedWhere.put(ENTITY_EMAIL, new FieldUserEmailClause());
        supportedWhere.put(COMMON_ENTITY_ID, new DomainIdQueryClause<>(USER_ID_LITERAL));
        supportedWhere.put(ENTITY_SUBSCRIPTION, new FieldBooleanEqualClause<>(ENTITY_SUBSCRIPTION));
        supportedWhere.put(ENTITY_GRANTED_AUTHORITIES, new FieldStringLikeClause<>(ENTITY_GRANTED_AUTHORITIES));
    }

}