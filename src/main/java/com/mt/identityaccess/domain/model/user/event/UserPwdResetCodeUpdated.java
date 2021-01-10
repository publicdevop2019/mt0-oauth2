package com.mt.identityaccess.domain.model.user.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.user.PasswordResetCode;
import com.mt.identityaccess.domain.model.user.UserEmail;
import com.mt.identityaccess.domain.model.user.UserId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

public class UserPwdResetCodeUpdated extends DomainEvent {
    private String email;
    private String code;

    public UserPwdResetCodeUpdated(UserId userId, UserEmail email, PasswordResetCode pwdResetToken) {
        super(userId);
        setEmail(email);
        setCode(pwdResetToken);
    }

    public void setEmail(UserEmail userEmail) {
        this.email = userEmail.getEmail();
    }

    public void setCode(PasswordResetCode passwordResetCode) {
        this.code = passwordResetCode.getValue();
    }
}
