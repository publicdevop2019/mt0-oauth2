package com.mt.common.domain.model.id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@NoArgsConstructor
@MappedSuperclass
public class DomainId implements Serializable {
    @Getter
    @Column(unique = true, updatable = false)
    @Setter(AccessLevel.PROTECTED)
    private String domainId;

    public DomainId(String domainId) {
        this.domainId = domainId;
    }
}
