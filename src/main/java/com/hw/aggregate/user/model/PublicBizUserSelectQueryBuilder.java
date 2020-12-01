package com.hw.aggregate.user.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldEmailEqualClause;
import org.springframework.stereotype.Component;

import static com.hw.aggregate.user.model.BizUser.ENTITY_EMAIL;

@Component
public class PublicBizUserSelectQueryBuilder extends SelectQueryBuilder<BizUser> {
    {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
        supportedWhereField.put("email", new SelectFieldEmailEqualClause<>(ENTITY_EMAIL));
    }

}
