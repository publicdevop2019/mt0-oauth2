package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

public class PendingUserCreated extends DomainEvent {
    public PendingUserCreated(RegistrationEmail email) {
        super(email);
    }
}
