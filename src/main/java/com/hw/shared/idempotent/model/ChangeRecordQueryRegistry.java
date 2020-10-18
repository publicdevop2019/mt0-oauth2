package com.hw.shared.idempotent.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class ChangeRecordQueryRegistry extends RestfulQueryRegistry<ChangeRecord> {

    @Override
    public Class<ChangeRecord> getEntityClass() {
        return ChangeRecord.class;
    }

}
