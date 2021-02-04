package com.mt.identityaccess.domain.model.user;

import com.mt.common.validate.Validator;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
public class UserEmail {
    @Getter
    private String email;

    public UserEmail(String email) {
        setEmail(email);
    }

    private void setEmail(String email) {
        this.email = email;
        Validator.notNull(email);
        Validator.notBlank(email);
        Validator.isEmail(email);
    }
}
