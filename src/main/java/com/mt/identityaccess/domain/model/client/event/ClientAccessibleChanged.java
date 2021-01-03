package com.mt.identityaccess.domain.model.client.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class ClientAccessibleChanged extends DomainEvent{
    public ClientAccessibleChanged(ClientId clientId) {
        super(clientId);
    }
}
