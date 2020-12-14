package com.hw.domain.model.user;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldBooleanEqualClause;
import com.hw.shared.sql.clause.SelectFieldEmailEqualClause;
import org.springframework.stereotype.Component;

import static com.hw.domain.model.user.User.ENTITY_EMAIL;
import static com.hw.domain.model.user.User.ENTITY_SUBSCRIPTION;

@Component
public class AppBizUserSelectQueryBuilder extends SelectQueryBuilder<User> {
    {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
        supportedWhereField.put("email", new SelectFieldEmailEqualClause<>(ENTITY_EMAIL));
        supportedWhereField.put("subscription", new SelectFieldBooleanEqualClause<>(ENTITY_SUBSCRIPTION));
    }

}
