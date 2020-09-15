package com.hw.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.shared.EurekaRegistryHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.hw.shared.AppConstant.HTTP_HEADER_CHANGE_ID;

public class CommonTokenRevocationService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SelfSignedTokenConfig authTokenHelper;

    @Autowired
    private EurekaRegistryHelper eurekaRegistryHelper;

    protected void blacklist(String url, Long id, TokenTypeEnum tokenTypeEnum) {
        String resolvedUrl = eurekaRegistryHelper.getProxyHomePageUrl() + url;
        CreateRevokeTokenCommand createRevokeTokenCommand = new CreateRevokeTokenCommand();
        createRevokeTokenCommand.setId(id);
        createRevokeTokenCommand.setType(tokenTypeEnum);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authTokenHelper.getSelfSignedAccessToken().getValue());
        headers.set(HTTP_HEADER_CHANGE_ID, UUID.randomUUID().toString());
        HttpEntity<CreateRevokeTokenCommand> hashMapHttpEntity = new HttpEntity<>(createRevokeTokenCommand, headers);
        try {
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity, String.class);
        } catch (HttpClientErrorException ex) {
            /**
             * re-try
             */
            headers.setBearerAuth(authTokenHelper.getSelfSignedAccessToken().getValue());
            HttpEntity<CreateRevokeTokenCommand> hashMapHttpEntity2 = new HttpEntity<>(createRevokeTokenCommand, headers);
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity2, String.class);

        }
    }

    public enum TokenTypeEnum {
        CLIENT,
        USER
    }

    @Data
    public class CreateRevokeTokenCommand {
        private Long id;
        private TokenTypeEnum type;
    }
}
