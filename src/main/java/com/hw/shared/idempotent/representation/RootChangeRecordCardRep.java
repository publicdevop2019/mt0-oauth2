package com.hw.shared.idempotent.representation;

import com.hw.shared.idempotent.OperationType;
import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.idempotent.model.CustomByteArraySerializer;
import lombok.Data;

@Data
public class RootChangeRecordCardRep {
    private Long id;

    private String changeId;
    private String entityType;

    private OperationType operationType;
    private String query;
    private String serviceBeanName;
    private Object requestBody;

    public RootChangeRecordCardRep(ChangeRecord changeRecord) {
        this.id = changeRecord.getId();
        this.changeId = changeRecord.getChangeId();
        this.entityType = changeRecord.getEntityType();
        this.operationType = changeRecord.getOperationType();
        this.query = changeRecord.getQuery();
        this.serviceBeanName = changeRecord.getServiceBeanName();
        this.requestBody = CustomByteArraySerializer.convertToEntityAttribute(changeRecord.getRequestBody());
    }
}
