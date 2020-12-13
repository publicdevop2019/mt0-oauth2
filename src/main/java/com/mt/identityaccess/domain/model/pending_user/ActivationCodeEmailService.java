package com.mt.identityaccess.domain.model.pending_user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.config.CommonEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ActivationCodeEmailService {

    @Value("${url.notify.register}")
    private String register;


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommonEmailService commonEmailService;

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
        commonEmailService.send(body, register);

    }


}
