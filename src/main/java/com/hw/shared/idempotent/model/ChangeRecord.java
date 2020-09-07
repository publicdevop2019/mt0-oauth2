package com.hw.shared.idempotent.model;

import com.hw.shared.Auditable;
import com.hw.shared.idempotent.command.CreateChangeRecordCommand;
import com.hw.shared.rest.IdBasedEntity;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"changeId", "entityType"}))
@Data
@NoArgsConstructor
public class ChangeRecord extends Auditable implements IdBasedEntity {
    @Id
    private Long id;

    @Column(nullable = false)
    private String changeId;
    @Column(nullable = false)
    private String entityType;
    @Column(nullable = false)
    private String serviceBeanName;

    @Column(length = 100000)
    private ArrayList<PatchCommand> patchCommands;

    @Column(length = 100000)
    private CreateDeleteCommand createDeleteCommand;

    public static ChangeRecord create(CreateChangeRecordCommand command) {
        return new ChangeRecord(command);
    }

    private ChangeRecord(CreateChangeRecordCommand command) {
        this.id = command.getId();
        this.changeId = command.getChangeId();
        this.entityType = command.getEntityType();
        this.patchCommands = command.getPatchCommands();
        this.createDeleteCommand = command.getCreateDeleteCommand();
    }
}
