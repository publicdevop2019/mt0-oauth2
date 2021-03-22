package com.mt.access.domain.model.endpoint.event;

public class EndpointReloadRequested extends EndpointEvent {
    public EndpointReloadRequested() {
        super();
        setInternal(false);
    }
}
