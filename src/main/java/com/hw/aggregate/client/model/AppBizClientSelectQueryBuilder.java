package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldLongEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class AppBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    AppBizClientSelectQueryBuilder() {
        supportedWhereField.put("clientId", new SelectFieldLongEqualClause<>(COMMON_ENTITY_ID));
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

}
