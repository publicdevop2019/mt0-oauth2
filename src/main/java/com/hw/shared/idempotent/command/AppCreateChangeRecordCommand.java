package com.hw.shared.idempotent.command;

import com.hw.shared.idempotent.OperationType;
import lombok.Data;

import java.util.List;

@Data
public class AppCreateChangeRecordCommand {

    private String changeId;
    private String entityType;
    private String serviceBeanName;
    private OperationType operationType;
    private String query;
    private Object replacedVersion;
    private Object requestBody;
    private List<Long> deletedIds;
}
