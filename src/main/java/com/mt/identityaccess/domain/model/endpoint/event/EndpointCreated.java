package com.mt.identityaccess.domain.model.endpoint.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.endpoint.EndpointId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

public class EndpointCreated extends DomainEvent {
    public EndpointCreated(EndpointId endpointId) {
        super(endpointId);
    }
}
