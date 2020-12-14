package com.hw.port.adapter.service.revoke_token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.shared.EurekaRegistryHelper;
import com.hw.infrastructure.service.SelfSignedJwtTokenService;
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

import java.util.UUID;

import static com.hw.shared.AppConstant.HTTP_HEADER_CHANGE_ID;

@Component
public class HttpRevokeTokenAdapter implements RevokeTokenAdapter {
    @Value("${url.zuul.revoke-tokens}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SelfSignedJwtTokenService selfSignedJwtTokenService;

    @Autowired
    private EurekaRegistryHelper eurekaRegistryHelper;

    @Async
    @Override
    public void revoke(String id, String targetType) {
        String resolvedUrl = eurekaRegistryHelper.getProxyHomePageUrl() + url;
        CreateRevokeTokenCommand createRevokeTokenCommand = new CreateRevokeTokenCommand(id, targetType);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(selfSignedJwtTokenService.getSelfSignedAccessToken().getValue());
        headers.set(HTTP_HEADER_CHANGE_ID, UUID.randomUUID().toString());
        HttpEntity<CreateRevokeTokenCommand> hashMapHttpEntity = new HttpEntity<>(createRevokeTokenCommand, headers);
        try {
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity, String.class);
        } catch (HttpClientErrorException ex) {
            /**
             * re-try
             */
            headers.setBearerAuth(selfSignedJwtTokenService.getSelfSignedAccessToken().getValue());
            HttpEntity<CreateRevokeTokenCommand> hashMapHttpEntity2 = new HttpEntity<>(createRevokeTokenCommand, headers);
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity2, String.class);

        }
    }

    public static class CreateRevokeTokenCommand {
        private final String id;
        private final String type;

        public CreateRevokeTokenCommand(String id, String type) {
            this.id = id;
            this.type = type;
        }
    }
}
