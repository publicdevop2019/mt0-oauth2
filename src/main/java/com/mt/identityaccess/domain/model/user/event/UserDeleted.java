package com.mt.identityaccess.domain.model.user.event;

import com.mt.identityaccess.domain.model.user.UserId;

public class UserDeleted extends UserEvent {
    public UserDeleted(UserId userId) {
        super(userId);
    }
}
