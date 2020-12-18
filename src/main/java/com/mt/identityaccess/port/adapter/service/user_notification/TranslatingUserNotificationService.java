package com.mt.identityaccess.port.adapter.service.user_notification;

import com.mt.identityaccess.domain.model.pending_user.ActivationCode;
import com.mt.identityaccess.domain.model.user.ResetCode;
import com.mt.identityaccess.domain.model.UserNotificationService;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.UserEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TranslatingUserNotificationService implements UserNotificationService {

    @Autowired
    private UserMessageAdapter deliverUserMessageAdapter;

    @Override
    public void sendActivationCodeTo(RegistrationEmail email, ActivationCode activationCode) {
        deliverUserMessageAdapter.sendActivationCode(email, activationCode);
    }

    @Override
    public void sendPasswordResetCodeTo(UserEmail email, ResetCode resetCode) {
        deliverUserMessageAdapter.sendPasswordResetCode(email, resetCode);
    }
}
