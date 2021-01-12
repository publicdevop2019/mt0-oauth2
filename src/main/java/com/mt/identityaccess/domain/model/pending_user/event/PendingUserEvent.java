package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain_event.DomainEvent;

import java.util.Set;

public class PendingUserEvent extends DomainEvent {
    public static final String TOPIC_PENDING_USER = "endpoint";

    public PendingUserEvent() {
        super();
        setTopic(TOPIC_PENDING_USER);
    }

    public PendingUserEvent(DomainId domainId) {
        super(domainId);
        setTopic(TOPIC_PENDING_USER);
    }

    public PendingUserEvent(Set<DomainId> domainIds) {
        super(domainIds);
        setTopic(TOPIC_PENDING_USER);
    }
}
