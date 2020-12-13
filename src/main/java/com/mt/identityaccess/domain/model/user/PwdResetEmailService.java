package com.mt.identityaccess.domain.model.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.config.CommonEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PwdResetEmailService {
    @Autowired
    private CommonEmailService commonEmailService;

    @Value("${url.notify.pwdReset}")
    private String pwdReset;

    @Autowired
    private ObjectMapper mapper;

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
        commonEmailService.send(body, pwdReset);
    }

}
