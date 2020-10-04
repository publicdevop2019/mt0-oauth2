package com.hw.shared.idempotent.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldStringEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.shared.idempotent.model.ChangeRecord.CHANGE_ID;
import static com.hw.shared.idempotent.model.ChangeRecord.ENTITY_TYPE;

@Component
public class RootChangeRecordSelectQueryBuilder extends SelectQueryBuilder<ChangeRecord> {
    RootChangeRecordSelectQueryBuilder() {
        supportedWhereField.put(ENTITY_TYPE, new SelectFieldStringEqualClause<>(ENTITY_TYPE));
        supportedWhereField.put(CHANGE_ID, new SelectFieldStringEqualClause<>(CHANGE_ID));
        allowEmptyClause = true;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
