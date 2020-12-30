package com.mt.identityaccess.port.adapter.service.user_notification;

import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.PasswordResetToken;
import com.mt.identityaccess.domain.model.user.UserEmail;

public interface UserMessageAdapter {
    void sendActivationCode(RegistrationEmail email, ActivationCode activationCode);

    void sendPasswordResetCode(UserEmail email, PasswordResetToken resetCode);
}