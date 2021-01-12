package com.mt.identityaccess.domain.model.endpoint.event;

import com.mt.identityaccess.domain.model.endpoint.EndpointId;

public class EndpointCreated extends EndpointEvent {
    public EndpointCreated(EndpointId endpointId) {
        super(endpointId);
        setInternal(false);
    }
}
