package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;

public class UserCreated extends UserEvent {
    public UserCreated(UserId userId) {
        super(userId);
    }
}
