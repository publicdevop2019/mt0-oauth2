package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;

public class PendingUserCreated extends PendingUserEvent {
    public PendingUserCreated(RegistrationEmail email) {
        super(email);
    }
}
