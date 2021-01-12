package com.mt.identityaccess.domain.model.user.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.user.UserId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

public class UserUpdated extends UserEvent {
    public UserUpdated(UserId userId) {
        super(userId);
    }
}
