package com.hw.shared.idempotent.model;

import com.hw.shared.Auditable;
import com.hw.shared.idempotent.OperationType;
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
    public static final String ENTITY_TYPE = "entityType";
    @Column(nullable = false)
    private String serviceBeanName;

    @Column(length = 100000)
    private ArrayList<PatchCommand> patchCommands;

    private OperationType operationType;
    private String query;

}
