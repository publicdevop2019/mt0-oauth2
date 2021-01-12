package com.mt.identityaccess.domain.model.client.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain_event.DomainEvent;

import java.util.Set;

public class ClientEvent extends DomainEvent {
    public static final String TOPIC_CLIENT = "client";
    public ClientEvent() {
        super();
        this.setTopic(TOPIC_CLIENT);
    }

    public ClientEvent(DomainId domainId) {
        super(domainId);
        this.setTopic(TOPIC_CLIENT);
    }

    public ClientEvent(Set<DomainId> domainIds) {
        super(domainIds);
        this.setTopic(TOPIC_CLIENT);
    }
}
