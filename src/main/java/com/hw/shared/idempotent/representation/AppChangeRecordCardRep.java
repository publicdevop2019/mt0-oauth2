package com.hw.shared.idempotent.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.hw.shared.idempotent.OperationType;
import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.idempotent.model.CustomByteArraySerializer;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;

@Data
public class AppChangeRecordCardRep {
    private Long id;

    private String changeId;
    private String entityType;
    private String serviceBeanName;

    private ArrayList<Long> deletedIds;
    private OperationType operationType;
    private String query;
    private Object replacedVersion;
    private Object requestBody;

    public AppChangeRecordCardRep(ChangeRecord changeRecord, ObjectMapper om) {
        this.id = changeRecord.getId();
        this.changeId = changeRecord.getChangeId();
        this.entityType = changeRecord.getEntityType();
        this.serviceBeanName = changeRecord.getServiceBeanName();
        this.operationType = changeRecord.getOperationType();
        this.query = changeRecord.getQuery();
        this.deletedIds = changeRecord.getDeletedIds();
        this.replacedVersion = CustomByteArraySerializer.convertToEntityAttribute(changeRecord.getReplacedVersion());
        if (changeRecord.getOperationType().equals(OperationType.PATCH_BY_ID)) {
            try {
                this.requestBody = om.readValue(changeRecord.getRequestBody(), JsonPatch.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.requestBody = CustomByteArraySerializer.convertToEntityAttribute(changeRecord.getRequestBody());
        }
    }
}
