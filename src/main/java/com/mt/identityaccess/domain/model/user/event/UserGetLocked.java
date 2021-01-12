package com.mt.identityaccess.domain.model.user.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.user.UserId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

public class UserGetLocked extends UserEvent {
    public UserGetLocked(UserId userId) {
        super(userId);
    }
}
