package com.hw.aggregate.user.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldBooleanEqualClause;
import com.hw.shared.sql.clause.SelectFieldEmailEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.user.model.BizUser.ENTITY_EMAIL;
import static com.hw.aggregate.user.model.BizUser.ENTITY_SUBSCRIPTION;

@Component
public class AppBizUserSelectQueryBuilder extends SelectQueryBuilder<BizUser> {
    AppBizUserSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
        supportedWhereField.put("email", new SelectFieldEmailEqualClause<>(ENTITY_EMAIL));
        supportedWhereField.put("subscription", new SelectFieldBooleanEqualClause<>(ENTITY_SUBSCRIPTION));
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
