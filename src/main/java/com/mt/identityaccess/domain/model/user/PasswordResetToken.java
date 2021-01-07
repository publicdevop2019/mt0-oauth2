package com.mt.identityaccess.domain.model.user;

import com.google.common.base.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordResetToken)) return false;
        PasswordResetToken that = (PasswordResetToken) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
