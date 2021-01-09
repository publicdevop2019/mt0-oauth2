package com.mt.identityaccess.domain.model.user;

import com.google.common.base.Objects;
import com.mt.identityaccess.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class PasswordResetCode {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private String value;

    public PasswordResetCode() {
        setValue(DomainRegistry.passwordResetTokenService().generate());
    }

    public PasswordResetCode(String token) {
        setValue(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordResetCode)) return false;
        PasswordResetCode that = (PasswordResetCode) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
