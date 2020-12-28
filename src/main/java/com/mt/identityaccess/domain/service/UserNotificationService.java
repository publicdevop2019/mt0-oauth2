package com.mt.identityaccess.domain.service;

import com.mt.identityaccess.domain.model.pending_user.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.ResetCode;
import com.mt.identityaccess.domain.model.user.UserEmail;

public interface UserNotificationService {
    void sendActivationCodeTo(RegistrationEmail email, ActivationCode activationCode);
    void sendPasswordResetCodeTo(UserEmail email, ResetCode resetCode);
}
