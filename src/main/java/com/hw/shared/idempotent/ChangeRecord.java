package com.hw.shared.idempotent;

import com.hw.shared.Auditable;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"changeId", "entityType"}))
@Data
public class ChangeRecord extends Auditable {
    @Id
    private Long id;

    @Column(nullable = false)
    private String changeId;
    @Column(nullable = false)
    private String entityType;

    @Column(length = 100000)
    private ArrayList<PatchCommand> patchCommands;

    @Column(length = 100000)
    private CreateDeleteCommand createDeleteCommands;

}
