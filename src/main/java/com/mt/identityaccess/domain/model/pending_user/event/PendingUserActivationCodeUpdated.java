package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class PendingUserActivationCodeUpdated extends DomainEvent {
    private String email;
    private String code;

    public PendingUserActivationCodeUpdated(RegistrationEmail registrationEmail, ActivationCode activationCode) {
        super(registrationEmail);
        setEmail(registrationEmail);
        setCode(activationCode);
        updateBody(this);
    }

    private void setEmail(RegistrationEmail registrationEmail) {
        this.email = registrationEmail.getEmail();
    }

    private void setCode(ActivationCode activationCode) {
        this.code = activationCode.getActivationCode();
    }
}
