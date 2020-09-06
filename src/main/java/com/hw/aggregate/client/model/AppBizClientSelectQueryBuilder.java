package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AppBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }

}
