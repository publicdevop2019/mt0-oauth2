package com.mt.identityaccess.domain.model.user.event;

import com.mt.common.domain.model.DomainEvent;
import com.mt.identityaccess.domain.model.user.UserId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class UserGetLocked extends DomainEvent {
    public UserGetLocked(UserId userId) {
        super(userId);
    }
}
