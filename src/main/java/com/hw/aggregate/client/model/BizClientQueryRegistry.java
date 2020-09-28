package com.hw.aggregate.client.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizClientQueryRegistry extends RestfulQueryRegistry<BizClient> {
    @Autowired
    private RootBizClientSelectQueryBuilder rootBizClientSelectQueryBuilder;
    @Autowired
    private AppBizClientSelectQueryBuilder appBizClientSelectQueryBuilder;
    @Autowired
    private RootBizClientDeleteQueryBuilder rootBizClientDeleteQueryBuilder;
    @Autowired
    private UserBizClientSelectQueryBuilder userBizClientSelectQueryBuilder;

    @Override
    @PostConstruct
    protected void configQueryBuilder() {
        selectQueryBuilder.put(RoleEnum.ROOT, rootBizClientSelectQueryBuilder);
        selectQueryBuilder.put(RoleEnum.APP, appBizClientSelectQueryBuilder);
        selectQueryBuilder.put(RoleEnum.USER, userBizClientSelectQueryBuilder);
        deleteQueryBuilder.put(RoleEnum.ROOT, rootBizClientDeleteQueryBuilder);
    }
}