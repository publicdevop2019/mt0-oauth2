package com.mt.identityaccess.domain.model.user.event;

import com.mt.identityaccess.domain.model.user.UserId;

public class UserAuthorityChanged extends UserEvent {
    public UserAuthorityChanged(UserId userId) {
        super(userId);
    }
}
