package com.hw.aggregate.user.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldBooleanEqualClause;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.user.model.BizUser.ENTITY_EMAIL;
import static com.hw.aggregate.user.model.BizUser.ENTITY_SUBSCRIPTION;
@Component
public class AdminBizUserSelectQueryBuilder extends SelectQueryBuilder<BizUser> {
    AdminBizUserSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 20;
        MAX_PAGE_SIZE = 50;
        mappedSortBy.put("email", ENTITY_EMAIL);
        mappedSortBy.put("createAt", "createdAt");
        supportedWhereField.put("email", new SelectFieldStringEqualClause<>(ENTITY_EMAIL));
        supportedWhereField.put("subscription", new SelectFieldBooleanEqualClause<>(ENTITY_SUBSCRIPTION));
        allowEmptyClause=true;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}