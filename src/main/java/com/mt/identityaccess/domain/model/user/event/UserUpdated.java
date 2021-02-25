package com.mt.identityaccess.domain.model.user.event;

import com.mt.identityaccess.domain.model.user.UserId;

public class UserUpdated extends UserEvent {
    public UserUpdated(UserId userId) {
        super(userId);
    }
}
