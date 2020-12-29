package com.mt.identityaccess.domain.model.user;

import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class UserEmail {
    @Getter
    private String email;

    public UserEmail(String email) {
        if (!StringUtils.hasText(email))
            throw new IllegalArgumentException("email is empty");
        Optional<User> user1 = DomainRegistry.userRepository().searchExistingUserWith(email);
        if (user1.isPresent())
            throw new IllegalArgumentException("already an user " + email);
        Optional<PendingUser> pendingUser = DomainRegistry.pendingUserRepository().pendingUserOfEmail(new RegistrationEmail(email));
        if (pendingUser.isEmpty())
            throw new IllegalArgumentException("please get activation code first");
        this.email = email;
    }
}
