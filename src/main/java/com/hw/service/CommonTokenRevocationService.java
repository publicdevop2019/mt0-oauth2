package com.hw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.clazz.AuthTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class CommonTokenRevocationService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AuthTokenHelper authTokenHelper;

    protected void blacklist(String url, String name, boolean shouldRevoke) {
        if (shouldRevoke) {
            HashMap<String, String> blockBody = new HashMap<>();
            blockBody.put("name", name);
            String body = null;
            try {
                body = mapper.writeValueAsString(blockBody);
            } catch (JsonProcessingException e) {
                /**
                 * this block is purposely left blank
                 */
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authTokenHelper.getSelfSignedAccessToken().getValue());
            HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(body, headers);
            try {
                restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity, String.class);
            } catch (HttpClientErrorException ex) {
                /**
                 * re-try
                 */
                headers.setBearerAuth(authTokenHelper.getSelfSignedAccessToken().getValue());
                HttpEntity<String> hashMapHttpEntity2 = new HttpEntity<>(body, headers);
                restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity2, String.class);

            }
        }
    }
}
