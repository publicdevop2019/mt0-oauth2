package com.mt.identityaccess.domain.model.user.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.user.UserId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class UserCreated extends DomainEvent {
    public UserCreated(UserId userId) {
        super(userId);
    }
}
