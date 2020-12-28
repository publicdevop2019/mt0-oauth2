package com.mt.identityaccess.domain.model.client;

import com.mt.common.domain.model.id.DomainId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class ClientId extends DomainId implements Serializable {
    @Column(unique = true, updatable = false)
    private String clientId;

    @Override
    public String getDomainId() {
        return clientId;
    }
}
