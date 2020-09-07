package com.hw.shared.idempotent.command;

import com.hw.shared.idempotent.model.CreateDeleteCommand;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;

import java.util.ArrayList;

@Data
public class CreateChangeRecordCommand {
    private Long id;

    private String changeId;
    private String entityType;

    private ArrayList<PatchCommand> patchCommands;

    private CreateDeleteCommand createDeleteCommand;
}
