package com.hw.shared.idempotent.representation;

import com.hw.shared.idempotent.OperationType;
import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;

import java.util.ArrayList;

@Data
public class RootChangeRecordCardRep {
    private Long id;

    private String changeId;
    private String entityType;

    private ArrayList<PatchCommand> patchCommands;
    private OperationType operationType;
    private String query;
    private String serviceBeanName;

    public RootChangeRecordCardRep(ChangeRecord changeRecord) {
        this.id = changeRecord.getId();
        this.changeId = changeRecord.getChangeId();
        this.entityType = changeRecord.getEntityType();
        this.patchCommands = changeRecord.getPatchCommands();
        this.operationType = changeRecord.getOperationType();
        this.query = changeRecord.getQuery();
        this.serviceBeanName = changeRecord.getServiceBeanName();
    }
}
