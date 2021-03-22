package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;

public class UserAuthorityChanged extends UserEvent {
    public UserAuthorityChanged(UserId userId) {
        super(userId);
    }
}
