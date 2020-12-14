package com.hw.domain.model;

import com.hw.domain.model.pending_user.ActivationCode;
import com.hw.domain.model.pending_user.RegistrationEmail;
import com.hw.domain.model.user.ResetCode;
import com.hw.domain.model.user.UserEmail;

public interface UserNotificationService {
    void sendActivationCodeTo(RegistrationEmail email, ActivationCode activationCode);
    void sendPasswordResetCodeTo(UserEmail email, ResetCode resetCode);
}
