package com.hw.port.adapter.service.user_notification;

import com.hw.domain.model.pending_user.ActivationCode;
import com.hw.domain.model.user.ResetCode;
import com.hw.domain.model.user.UserEmail;
import com.hw.domain.model.pending_user.RegistrationEmail;

public interface UserMessageAdapter {
    void sendActivationCode(RegistrationEmail email, ActivationCode activationCode);

    void sendPasswordResetCode(UserEmail email, ResetCode resetCode);
}
