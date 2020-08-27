package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;


@Component
public class UserBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    UserBizClientSelectQueryBuilder() {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
