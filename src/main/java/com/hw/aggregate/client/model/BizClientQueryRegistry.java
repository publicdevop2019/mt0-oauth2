package com.hw.aggregate.client.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizClientQueryRegistry extends RestfulQueryRegistry<BizClient> {
    @Autowired
    private UserBizClientSelectQueryBuilder customerClientSelectQueryBuilder;
    @Autowired
    private RootBizClientSelectQueryBuilder rootBizClientSelectQueryBuilder;
    @Autowired
    private RootBizClientDeleteQueryBuilder rootBizClientDeleteQueryBuilder;

    @Override
    @PostConstruct
    protected void configQueryBuilder() {
        selectQueryBuilder.put(RoleEnum.USER, customerClientSelectQueryBuilder);
        selectQueryBuilder.put(RoleEnum.ROOT, rootBizClientSelectQueryBuilder);
        deleteQueryBuilder.put(RoleEnum.ROOT, rootBizClientDeleteQueryBuilder);
    }
}
