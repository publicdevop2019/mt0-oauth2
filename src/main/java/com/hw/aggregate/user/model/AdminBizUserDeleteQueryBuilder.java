package com.hw.aggregate.user.model;

import com.hw.shared.sql.builder.DeleteByIdQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class AdminBizUserDeleteQueryBuilder extends DeleteByIdQueryBuilder {
    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        em = entityManager;
    }
}
