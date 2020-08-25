package com.hw.aggregate.pending_user.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PendingUserQueryRegistry extends RestfulQueryRegistry<PendingUser> {
    @Autowired
    AppPendingUserSelectQueryBuilder appPendingUserSelectQueryBuilder;
    @Override
    protected void configQueryBuilder() {
selectQueryBuilder.put(RoleEnum.APP,appPendingUserSelectQueryBuilder);
    }
}
