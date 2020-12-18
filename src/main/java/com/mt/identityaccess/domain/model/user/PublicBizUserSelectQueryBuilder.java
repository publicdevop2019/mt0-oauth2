package com.mt.identityaccess.domain.model.user;

import com.mt.common.sql.builder.SelectQueryBuilder;
import com.mt.common.sql.clause.SelectFieldEmailEqualClause;
import org.springframework.stereotype.Component;

import static com.mt.identityaccess.domain.model.user.User.ENTITY_EMAIL;

@Component
public class PublicBizUserSelectQueryBuilder extends SelectQueryBuilder<User> {
    {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
        supportedWhereField.put("email", new SelectFieldEmailEqualClause<>(ENTITY_EMAIL));
    }

}
