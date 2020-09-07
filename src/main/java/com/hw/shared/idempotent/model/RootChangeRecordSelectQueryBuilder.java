package com.hw.shared.idempotent.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class RootChangeRecordSelectQueryBuilder extends SelectQueryBuilder<ChangeRecord> {
    RootChangeRecordSelectQueryBuilder(){
        allowEmptyClause=true;
    }
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
