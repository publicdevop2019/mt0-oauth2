package com.mt.identityaccess.domain.service;

import com.mt.common.validate.ValidationNotificationHandler;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.domain.model.user.UserEmail;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PendingUserValidationService {
    public void validate(PendingUser pendingUser, ValidationNotificationHandler handler) {
        Optional<User> user = DomainRegistry.userRepository().searchExistingUserWith(new UserEmail(pendingUser.getRegistrationEmail().getEmail()));
        if (user.isPresent())
            handler.handleError("already an user " + pendingUser.getRegistrationEmail().getEmail());
    }
}
