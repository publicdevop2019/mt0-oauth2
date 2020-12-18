package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.config.DomainEvent;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
public class ClientsBatchRemoved extends DomainEvent{
    public ClientsBatchRemoved(Set<ClientId> clientIds) {
        super(clientIds);
    }
}
