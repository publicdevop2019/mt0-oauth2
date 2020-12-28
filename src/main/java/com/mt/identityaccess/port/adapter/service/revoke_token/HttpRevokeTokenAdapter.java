package com.mt.identityaccess.port.adapter.service.revoke_token;

import com.mt.common.EurekaRegistryHelper;
import com.mt.identityaccess.infrastructure.SelfSignedJwtTokenService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.mt.common.AppConstant.HTTP_HEADER_CHANGE_ID;

@Component
@Slf4j
public class HttpRevokeTokenAdapter implements RevokeTokenAdapter {
    @Value("${url.zuul.revoke-tokens}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SelfSignedJwtTokenService selfSignedJwtTokenService;

    @Autowired
    private EurekaRegistryHelper eurekaRegistryHelper;

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
            log.debug("revoking token for {} type of {}", id, targetType);
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity, String.class);
        } catch (HttpClientErrorException ex) {
            /**
             * re-try
             */
            log.debug("re-try revoking token");
            headers.setBearerAuth(selfSignedJwtTokenService.getSelfSignedAccessToken().getValue());
            HttpEntity<CreateRevokeTokenCommand> hashMapHttpEntity2 = new HttpEntity<>(createRevokeTokenCommand, headers);
            restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity2, String.class);

        }
        log.debug("revoking token complete");
    }
    @Getter
    @Setter
    public static class CreateRevokeTokenCommand {
        private final String id;
        private final String type;

        public CreateRevokeTokenCommand(String id, String type) {
            this.id = id;
            this.type = type;
        }
    }
}
