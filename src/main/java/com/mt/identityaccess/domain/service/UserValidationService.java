package com.mt.identityaccess.domain.service;

import com.mt.common.validate.ValidationNotificationHandler;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserValidationService {
    public void validate(User user, ValidationNotificationHandler handler) {
        Optional<PendingUser> pendingUser = DomainRegistry.pendingUserRepository().pendingUserOfEmail(new RegistrationEmail(user.getEmail().getEmail()));
        if (pendingUser.isEmpty())
            throw new IllegalArgumentException("please get activation code first");
        Optional<User> user1 = DomainRegistry.userRepository().searchExistingUserWith(user.getEmail());
        if (user1.isPresent())
            throw new IllegalArgumentException("already an user " + user.getEmail().getEmail());
    }
}