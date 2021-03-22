package com.mt.access.domain.model.user.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;

import java.util.Set;

public abstract class UserEvent extends DomainEvent {
    public static final String TOPIC_USER = "user";
    public UserEvent() {
        super();
        setTopic(TOPIC_USER );
    }

    public UserEvent(DomainId domainId) {
        super(domainId);
        setTopic(TOPIC_USER );
    }

    public UserEvent(Set<DomainId> domainIds) {
        super(domainIds);
        setTopic(TOPIC_USER );
    }
}
