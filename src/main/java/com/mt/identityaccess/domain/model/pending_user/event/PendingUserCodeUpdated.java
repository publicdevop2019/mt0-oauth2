package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.common.domain.model.DomainEvent;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
@Entity
@NoArgsConstructor
public class PendingUserCodeUpdated extends DomainEvent {
    public PendingUserCodeUpdated(RegistrationEmail email) {
        super(email);
    }
}
