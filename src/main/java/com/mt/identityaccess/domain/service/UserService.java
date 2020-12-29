package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.*;
import com.mt.identityaccess.domain.model.user.event.UserCreated;
import com.mt.identityaccess.domain.model.user.event.UserPasswordChanged;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class UserService {
    public UserId create(UserEmail userEmail, String password, ActivationCode activationCode, UserId userId) {
        Optional<PendingUser> pendingUser = DomainRegistry.pendingUserRepository().pendingUserOfEmail(new RegistrationEmail(userEmail.getEmail()));
        if (pendingUser.isPresent()) {
            if (pendingUser.get().getActivationCode() == null || !pendingUser.get().getActivationCode().getActivationCode().equals(activationCode.getActivationCode()))
                throw new IllegalArgumentException("activation code mismatch");
            User user = new User(userEmail, password, userId);
            DomainRegistry.userRepository().add(user);
            DomainEventPublisher.instance().publish(new UserCreated(user.getUserId()));
            return user.getUserId();
        } else {
            return null;
        }
    }

    public void updatePassword(User user, String currentPwd, String password) {
        if (!StringUtils.hasText(password) || !StringUtils.hasText(currentPwd))
            throw new IllegalArgumentException("password(s)");
        if (!user.getPassword().sameAs(currentPwd))
            throw new IllegalArgumentException("wrong password");
        user.setPassword(new UserPassword(password));
        DomainRegistry.userRepository().add(user);
        DomainEventPublisher.instance().publish(new UserPasswordChanged(user.getUserId()));
    }

    public void forgetPassword(String email) {
        Optional<User> user = DomainRegistry.userRepository().searchExistingUserWith(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user does not exist");
        }
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        user.get().setPwdResetToken(passwordResetToken);
        DomainRegistry.userNotificationService().sendPasswordResetCodeTo(user.get().getEmail(), passwordResetToken);
        DomainRegistry.userRepository().add(user.get());

    }

    public void resetPassword(String email, String newPassword, String token) {
        Optional<User> user = DomainRegistry.userRepository().searchExistingUserWith(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user does not exist");
        }
        if (user.get().getPwdResetToken() == null)
            throw new IllegalArgumentException("token not exist");
        if (!user.get().getPwdResetToken().equals(new PasswordResetToken(token)))
            throw new IllegalArgumentException("token mismatch");
        user.get().setPassword(new UserPassword(newPassword));
        DomainRegistry.userRepository().add(user.get());
        DomainEventPublisher.instance().publish(new UserPasswordChanged(user.get().getUserId()));
    }

}
