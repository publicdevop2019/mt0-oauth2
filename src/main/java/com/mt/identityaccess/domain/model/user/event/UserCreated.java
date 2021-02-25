package com.mt.identityaccess.domain.model.user.event;

import com.mt.identityaccess.domain.model.user.UserId;

public class UserCreated extends UserEvent {
    public UserCreated(UserId userId) {
        super(userId);
    }
}
