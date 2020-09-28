package com.hw.shared.idempotent.command;

import com.hw.shared.idempotent.OperationType;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppCreateChangeRecordCommand {

    private String changeId;
    private String entityType;
    private String serviceBeanName;

    private ArrayList<PatchCommand> patchCommands;

    private OperationType operationType;
    private String query;
    private Object replacedVersion;
    private List<Long> deletedIds;
}
