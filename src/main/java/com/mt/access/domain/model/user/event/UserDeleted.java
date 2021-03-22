package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;

public class UserDeleted extends UserEvent {
    public UserDeleted(UserId userId) {
        super(userId);
    }
}
