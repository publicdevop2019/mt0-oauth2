package com.mt.identityaccess.domain.model.user;

import com.mt.identityaccess.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
@NoArgsConstructor
public class UserPassword {
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String password;

    public UserPassword(String password) {
        if (!StringUtils.hasText(password))
            throw new IllegalArgumentException("password is empty");
        setPassword(DomainRegistry.encryptionService().encryptedValue(password));
    }

    public boolean sameAs(String rawPassword) {
        return DomainRegistry.encryptionService().compare(password, rawPassword);
    }
}
