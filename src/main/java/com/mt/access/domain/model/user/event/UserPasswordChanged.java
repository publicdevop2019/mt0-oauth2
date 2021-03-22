package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;

public class UserPasswordChanged extends UserEvent {
    public UserPasswordChanged(UserId userId) {
        super(userId);
    }
}
