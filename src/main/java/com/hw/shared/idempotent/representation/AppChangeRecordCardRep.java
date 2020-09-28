package com.hw.shared.idempotent.representation;

import com.hw.shared.idempotent.OperationType;
import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.idempotent.model.CustomByteArraySerializer;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AppChangeRecordCardRep {
    private Long id;

    private String changeId;
    private String entityType;
    private String serviceBeanName;

    private ArrayList<PatchCommand> patchCommands;
    private ArrayList<Long> deletedIds;
    private OperationType operationType;
    private String query;
    private Object replacedVersion;

    public AppChangeRecordCardRep(ChangeRecord changeRecord) {
        this.id = changeRecord.getId();
        this.changeId = changeRecord.getChangeId();
        this.entityType = changeRecord.getEntityType();
        this.serviceBeanName = changeRecord.getServiceBeanName();
        this.patchCommands = changeRecord.getPatchCommands();
        this.operationType = changeRecord.getOperationType();
        this.query = changeRecord.getQuery();
        this.deletedIds = changeRecord.getDeletedIds();
        this.replacedVersion= CustomByteArraySerializer.convertToEntityAttribute(changeRecord.getReplacedVersion());
    }
}
