package com.hw.shared.idempotent.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChangeRecordQueryRegistry extends RestfulQueryRegistry<ChangeRecord> {
    @Autowired
    private RootChangeRecordSelectQueryBuilder changeRecordSelectQueryBuilder;

    @Override
    @PostConstruct
    protected void configQueryBuilder() {
        selectQueryBuilder.put(RoleEnum.ROOT, changeRecordSelectQueryBuilder);
    }
}
