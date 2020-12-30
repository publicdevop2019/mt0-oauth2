package com.mt.identityaccess.port.adapter.service.user_notification;

import com.mt.common.EurekaRegistryHelper;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.user.PasswordResetToken;
import com.mt.identityaccess.domain.model.user.UserEmail;
import com.mt.identityaccess.infrastructure.SelfSignedJwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class HttpUserMessageAdapter implements UserMessageAdapter {
    @Value("${url.notify.register}")
    private String registerUrl;
    @Value("${url.notify.pwdReset}")
    private String pwdResetUrl;
    @Autowired
    private SelfSignedJwtTokenService selfSignedTokenService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaRegistryHelper eurekaRegistryHelper;

    @Override
    public void sendActivationCode(RegistrationEmail email, ActivationCode activationCode) {
        HashMap<String, String> body = new HashMap<>();
        body.put("activationCode", activationCode.getActivationCode());
        body.put("email", email.getEmail());
        send(body, registerUrl);
    }

    @Override
    public void sendPasswordResetCode(UserEmail email, PasswordResetToken resetToken) {
        HashMap<String, String> body = new HashMap<>();
        body.put("token", resetToken.getValue());
        body.put("email", email.getEmail());
        send(body, pwdResetUrl);
    }

    public void send(Map<String, String> body, String url) {
        String resolvedUrl = eurekaRegistryHelper.getProxyHomePageUrl() + url;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(selfSignedTokenService.getSelfSignedAccessToken().getValue());
        HttpEntity<Map<String, String>> mapHttpEntity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, mapHttpEntity, String.class);
        } catch (HttpClientErrorException ex) {
            //retry when 401
            if (ex.getRawStatusCode() == 401) {
                headers.setBearerAuth(selfSignedTokenService.getSelfSignedAccessToken().getValue());
                restTemplate.exchange(resolvedUrl, HttpMethod.POST, mapHttpEntity, String.class);
            } else {
                throw new IllegalArgumentException("please wait for cool down");
            }

        }
    }

}