package com.mt.identityaccess.domain.model.user.event;

import com.mt.identityaccess.domain.model.user.UserId;

public class UserPasswordChanged extends UserEvent {
    public UserPasswordChanged(UserId userId) {
        super(userId);
    }
}
