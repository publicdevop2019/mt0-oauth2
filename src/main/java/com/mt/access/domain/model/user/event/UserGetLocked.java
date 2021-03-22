package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;

public class UserGetLocked extends UserEvent {
    public UserGetLocked(UserId userId) {
        super(userId);
    }
}
