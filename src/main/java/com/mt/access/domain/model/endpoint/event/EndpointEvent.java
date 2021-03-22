package com.mt.access.domain.model.endpoint.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;

import java.util.Set;

public abstract class EndpointEvent extends DomainEvent {
    public static final String TOPIC_ENDPOINT = "endpoint";
    public EndpointEvent() {
        super();
        setTopic(TOPIC_ENDPOINT);
    }

    public EndpointEvent(DomainId domainId) {
        super(domainId);
        setTopic(TOPIC_ENDPOINT);
    }

    public EndpointEvent(Set<DomainId> domainIds) {
        super(domainIds);
        setTopic(TOPIC_ENDPOINT);
    }
}
