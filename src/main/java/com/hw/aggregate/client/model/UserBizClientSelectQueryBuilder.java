package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.aggregate.client.model.BizClient.ENTITY_CLIENT_ID;

@Component
public class UserBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    UserBizClientSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
        supportedWhereField.put("clientId", new SelectFieldStringEqualClause<>(ENTITY_CLIENT_ID));
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
