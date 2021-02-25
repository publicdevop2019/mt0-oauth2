package com.mt.identityaccess.domain.model.user.event;

import com.mt.identityaccess.domain.model.user.UserId;

public class UserGetLocked extends UserEvent {
    public UserGetLocked(UserId userId) {
        super(userId);
    }
}
