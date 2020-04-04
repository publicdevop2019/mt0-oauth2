package com.hw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.clazz.AuthTokenHelper;
import com.hw.shared.EurekaRegistryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class EmailServiceImpl {

    @Value("${url.notify.register}")
    private String register;

    @Value("${url.notify.pwdReset}")
    private String pwdReset;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AuthTokenHelper authTokenHelper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    EurekaRegistryHelper eurekaRegistryHelper;

    @Async
    public void sendActivationCode(String activationCode, String email) {
        HashMap<String, String> blockBody = new HashMap<>();
        blockBody.put("activationCode", activationCode);
        blockBody.put("email", email);
        String body = null;
        try {
            body = mapper.writeValueAsString(blockBody);
        } catch (JsonProcessingException e) {
            /**
             * this block is purposely left blank
             */
        }
        send(body, register);

    }

    @Async
    public void sendPasswordResetLink(String token, String email) {
        HashMap<String, String> blockBody = new HashMap<>();
        blockBody.put("token", token);
        blockBody.put("email", email);
        String body = null;
        try {
            body = mapper.writeValueAsString(blockBody);
        } catch (JsonProcessingException e) {
            /**
             * this block is purposely left blank
             */
        }
        send(body, pwdReset);
    }

    private void send(String body, String url) {
        String resolvedUrl = eurekaRegistryHelper.getProxyHomePageUrl() + url;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authTokenHelper.getSelfSignedAccessToken().getValue());
        HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity, String.class);
        } catch (HttpClientErrorException ex) {
            /**
             * re-try
             */
            headers.setBearerAuth(authTokenHelper.getSelfSignedAccessToken().getValue());
            HttpEntity<String> hashMapHttpEntity2 = new HttpEntity<>(body, headers);
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity2, String.class);
        }
    }
}
