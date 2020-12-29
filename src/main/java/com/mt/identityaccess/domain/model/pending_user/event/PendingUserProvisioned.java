package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.common.domain.model.DomainEvent;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class PendingUserProvisioned  extends DomainEvent {
    public PendingUserProvisioned(RegistrationEmail email) {
        super(email);
    }
}
