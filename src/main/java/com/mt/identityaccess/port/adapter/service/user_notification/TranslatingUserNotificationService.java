package com.mt.identityaccess.port.adapter.service.user_notification;

import com.mt.common.EurekaRegistryHelper;
import com.mt.identityaccess.domain.model.pending_user.ActivationCode;
import com.mt.identityaccess.domain.model.user.ResetCode;
import com.mt.identityaccess.domain.service.UserNotificationService;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.UserEmail;
import com.mt.identityaccess.infrastructure.SelfSignedJwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class TranslatingUserNotificationService implements UserNotificationService {

    @Autowired
    private UserMessageAdapter userMessageAdapter;

    @Override
    public void sendActivationCodeTo(RegistrationEmail email, ActivationCode activationCode) {
        userMessageAdapter.sendActivationCode(email, activationCode);
    }

    @Override
    public void sendPasswordResetCodeTo(UserEmail email, ResetCode resetCode) {
        userMessageAdapter.sendPasswordResetCode(email, resetCode);
    }

}
