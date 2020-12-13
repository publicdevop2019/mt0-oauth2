package com.mt.identityaccess.domain.model.pending_user;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldEmailEqualClause;
import org.springframework.stereotype.Component;

import static com.mt.identityaccess.domain.model.user.BizUser.ENTITY_EMAIL;

@Component
public class AppPendingUserSelectQueryBuilder extends SelectQueryBuilder<PendingUser> {
    {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
        supportedWhereField.put("email", new SelectFieldEmailEqualClause<>(ENTITY_EMAIL));
    }
}
