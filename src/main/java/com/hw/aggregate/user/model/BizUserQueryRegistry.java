package com.hw.aggregate.user.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BizUserQueryRegistry extends RestfulQueryRegistry<BizUser> {
    @Autowired
    private AdminBizUserSelectQueryBuilder adminBizUserSelectQueryBuilder;
    @Autowired
    private AppBizUserSelectQueryBuilder appBizUserSelectQueryBuilder;
    @Autowired
    private UserBizUserSelectQueryBuilder userBizUserSelectQueryBuilder;
    @Autowired
    private AdminBizUserUpdateQueryBuilder adminBizUserUpdateQueryBuilder;
    @Autowired
    private AdminBizUserDeleteQueryBuilder adminBizUserDeleteQueryBuilder;
    @Autowired
    private PublicBizUserSelectQueryBuilder publicBizUserSelectQueryBuilder;

    @Override
    @PostConstruct
    protected void configQueryBuilder() {
        selectQueryBuilder.put(RoleEnum.ADMIN, adminBizUserSelectQueryBuilder);
        selectQueryBuilder.put(RoleEnum.APP, appBizUserSelectQueryBuilder);
        selectQueryBuilder.put(RoleEnum.USER, userBizUserSelectQueryBuilder);
        selectQueryBuilder.put(RoleEnum.PUBLIC, publicBizUserSelectQueryBuilder);
        updateQueryBuilder.put(RoleEnum.ADMIN, adminBizUserUpdateQueryBuilder);
        deleteQueryBuilder.put(RoleEnum.ADMIN, adminBizUserDeleteQueryBuilder);
    }
}
