package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;

public class UserUpdated extends UserEvent {
    public UserUpdated(UserId userId) {
        super(userId);
    }
}
