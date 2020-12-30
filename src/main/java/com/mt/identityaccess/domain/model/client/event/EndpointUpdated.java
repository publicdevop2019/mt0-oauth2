package com.mt.identityaccess.domain.model.client.event;

import com.mt.common.domain.model.DomainEvent;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.EndpointId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class EndpointUpdated extends DomainEvent {
    public EndpointUpdated(EndpointId endpointId) {
        super(endpointId);
    }
}
