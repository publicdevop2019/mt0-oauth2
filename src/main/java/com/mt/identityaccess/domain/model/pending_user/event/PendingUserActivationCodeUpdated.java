package com.mt.identityaccess.domain.model.pending_user.event;

import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import lombok.Getter;

@Getter
public class PendingUserActivationCodeUpdated extends PendingUserEvent {
    private String email;
    private String code;

    public PendingUserActivationCodeUpdated(RegistrationEmail registrationEmail, ActivationCode activationCode) {
        super(registrationEmail);
        setEmail(registrationEmail);
        setCode(activationCode);
        setInternal(false);
    }

    private void setEmail(RegistrationEmail registrationEmail) {
        this.email = registrationEmail.getEmail();
    }

    private void setCode(ActivationCode activationCode) {
        this.code = activationCode.getActivationCode();
    }
}
