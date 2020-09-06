package com.hw.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public class Auditable {

    @JsonIgnore
    @CreatedBy
    private String createdBy;

    @JsonIgnore
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JsonIgnore
    @LastModifiedBy
    private String modifiedBy;
    public static final String ENTITY_MODIFIED_BY = "modifiedBy";

    @JsonIgnore
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    public static final String ENTITY_MODIFIED_AT = "modifiedAt";

    private Boolean deleted;
    public static final String ENTITY_DELETED = "deleted";

    private String deletedBy;
    public static final String ENTITY_DELETED_BY = "deletedBy";

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    public static final String ENTITY_DELETED_AT = "deletedAt";

    private String restoredBy;
    public static final String ENTITY_RESTORED_BY = "restoredBy";

    @Temporal(TemporalType.TIMESTAMP)
    private Date restoredAt;
    public static final String ENTITY_RESTORED_AT = "restoredAt";

}
