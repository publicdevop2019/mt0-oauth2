package com.mt.access.domain.model.client.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.access.domain.model.client.ClientId;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class ClientEvent extends DomainEvent {
    public static final String TOPIC_CLIENT = "client";

    public ClientEvent() {
        super();
        this.setTopic(TOPIC_CLIENT);
    }

    public ClientEvent(ClientId domainId) {
        super(domainId);
        this.setTopic(TOPIC_CLIENT);
    }

    public ClientEvent(Set<ClientId> domainIds) {
        super(domainIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
        this.setTopic(TOPIC_CLIENT);
    }
}
