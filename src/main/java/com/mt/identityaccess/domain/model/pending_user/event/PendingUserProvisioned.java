package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.common.domain.model.DomainEvent;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;

public class PendingUserProvisioned  extends DomainEvent {
    public PendingUserProvisioned(RegistrationEmail email) {

    }
}
