package com.hw.shared.idempotent.model;

import com.hw.shared.Auditable;
import com.hw.shared.idempotent.OperationType;
import com.hw.shared.idempotent.command.AppCreateChangeRecordCommand;
import com.hw.shared.rest.IdBasedEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"changeId", "entityType"}))
@Data
@NoArgsConstructor
public class ChangeRecord extends Auditable implements IdBasedEntity {
    public static final String CHANGE_ID = "changeId";
    public static final String ENTITY_TYPE = "entityType";
    @Id
    private Long id;
    @Column(nullable = false)
    private String changeId;
    @Column(nullable = false)
    private String entityType;
    @Column(nullable = false)
    private String serviceBeanName;

    @Lob
    @Column(columnDefinition = "BLOB")
    //@Convert(converter = CustomByteArraySerializer.class)
    // not using converter due to lazy load , no session error
    private byte[] replacedVersion;
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] requestBody;

    private ArrayList<Long> deletedIds;
    private OperationType operationType;
    private String query;

    private ChangeRecord(Long id, AppCreateChangeRecordCommand command) {
        this.id = id;
        this.changeId = command.getChangeId();
        this.entityType = command.getEntityType();
        this.serviceBeanName = command.getServiceBeanName();
        this.requestBody = CustomByteArraySerializer.convertToDatabaseColumn(command.getRequestBody());
        this.operationType = command.getOperationType();
        this.query = command.getQuery();
        this.replacedVersion = CustomByteArraySerializer.convertToDatabaseColumn(command.getReplacedVersion());
        if (command.getDeletedIds() != null)
            this.deletedIds = new ArrayList<>(command.getDeletedIds());
    }

    public static ChangeRecord create(Long id, AppCreateChangeRecordCommand command) {
        return new ChangeRecord(id, command);
    }
}
