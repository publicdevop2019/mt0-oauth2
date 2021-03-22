package com.mt.access.domain.model.revoke_token.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.access.domain.model.revoke_token.RevokeTokenId;

public class RevokeTokenCreated extends DomainEvent {
    public RevokeTokenCreated(RevokeTokenId revokeTokenId) {
        super(revokeTokenId);
        setTopic("revokeToken");
    }
}
