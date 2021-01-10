package com.mt.identityaccess.domain.model.revoke_token.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.revoke_token.RevokeTokenId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

public class RevokeTokenCreated extends DomainEvent {
    public RevokeTokenCreated(RevokeTokenId revokeTokenId) {
        super(revokeTokenId);
    }
}
