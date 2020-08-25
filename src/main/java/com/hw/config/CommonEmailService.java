package com.hw.config;

import com.hw.shared.EurekaRegistryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonEmailService {

    @Autowired
    private SelfSignedTokenConfig authTokenHelper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaRegistryHelper eurekaRegistryHelper;

    public void send(String body, String url) {
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
            if (ex.getRawStatusCode() == 401) {
                headers.setBearerAuth(authTokenHelper.getSelfSignedAccessToken().getValue());
                HttpEntity<String> hashMapHttpEntity2 = new HttpEntity<>(body, headers);
                restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity2, String.class);
            } else {
                throw new IllegalArgumentException("please wait for cool down");
            }

        }
    }
}
