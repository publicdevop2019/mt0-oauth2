package com.hw.aggregate.pending_user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.config.CommonEmailService;
import com.hw.config.SelfSignedTokenConfig;
import com.hw.shared.BadRequestException;
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
