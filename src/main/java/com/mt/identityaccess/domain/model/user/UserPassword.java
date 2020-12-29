package com.mt.identityaccess.domain.model.user;

import com.mt.identityaccess.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

public class UserPassword {
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String password;

    public UserPassword(String password) {
        if (!StringUtils.hasText(password))
            throw new IllegalArgumentException("password is empty");
        setPassword(DomainRegistry.encryptionService().encryptedValue(password));
    }

    public boolean compareEncrypted(String currentPwd) {
        return DomainRegistry.encryptionService().compare(password, currentPwd);
    }
}
