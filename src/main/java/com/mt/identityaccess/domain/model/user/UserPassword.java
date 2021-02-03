package com.mt.identityaccess.domain.model.user;

import com.mt.common.validate.Validator;
import com.mt.identityaccess.domain.DomainRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserPassword {
    @Getter
    private String password;

    public UserPassword(String password) {
        setPassword(password);
    }

    private void setPassword(String password) {
        Validator.notNull(password);
        Validator.notBlank(password);
        this.password = DomainRegistry.encryptionService().encryptedValue(password);
    }
}
