package com.mt.identityaccess.port.adapter.service.user_notification;

import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.PasswordResetToken;
import com.mt.identityaccess.domain.model.user.UserEmail;
import com.mt.identityaccess.domain.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TranslatingUserNotificationService implements UserNotificationService {

    @Autowired
    private UserMessageAdapter userMessageAdapter;

    @Override
    public void sendActivationCodeTo(RegistrationEmail email, ActivationCode activationCode) {
        userMessageAdapter.sendActivationCode(email, activationCode);
    }

    @Override
    public void sendPasswordResetCodeTo(UserEmail email, PasswordResetToken resetCode) {
        userMessageAdapter.sendPasswordResetCode(email, resetCode);
    }

}
