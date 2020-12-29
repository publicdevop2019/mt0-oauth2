package com.mt.identityaccess.domain.model.user.event;

import com.mt.common.domain.model.DomainEvent;
import com.mt.identityaccess.domain.model.user.UserId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class UserDeleted extends DomainEvent {
    public UserDeleted(UserId userId) {
        super(userId);
    }
}
