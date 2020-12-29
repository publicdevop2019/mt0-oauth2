package com.mt.identityaccess.domain.model.user;

import com.mt.identityaccess.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class PasswordResetToken {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private String value;

    public PasswordResetToken() {
        setValue(DomainRegistry.passwordResetTokenService().generate());
    }

    public PasswordResetToken(String token) {
        setValue(token);
    }
}
