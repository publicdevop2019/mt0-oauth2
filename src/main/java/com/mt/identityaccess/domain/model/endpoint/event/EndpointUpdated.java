package com.mt.identityaccess.domain.model.endpoint.event;

import com.mt.identityaccess.domain.model.endpoint.EndpointId;

public class EndpointUpdated extends EndpointEvent {
    public EndpointUpdated(EndpointId endpointId) {
        super(endpointId);
        setInternal(false);
    }
}
