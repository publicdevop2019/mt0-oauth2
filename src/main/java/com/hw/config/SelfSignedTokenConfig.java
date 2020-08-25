package com.hw.config;

import com.hw.aggregate.client.BizClientRepo;
import com.hw.aggregate.client.model.BizClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.hw.aggregate.client.model.GrantTypeEnum.client_credentials;

/**
 * this class is only for authentication server itself
 */
@Component
public class SelfSignedTokenConfig {

    @Autowired
    private BizClientRepo clientRepo;

    @Autowired
    private TokenGranter tokenGranter;

    public OAuth2AccessToken getSelfSignedAccessToken() {
        Optional<BizClient> byId = clientRepo.findByClientId("oauth2-id");
        if (byId.isEmpty())
            throw new IllegalArgumentException("root authorization client not found!,this should never happen");
        BizClient client = byId.get();
        TokenRequest tokenRequest = new TokenRequest(null, client.getClientId(), client.getScope(), client_credentials.name());
        OAuth2AccessToken grant = tokenGranter.grant(client_credentials.name(), tokenRequest);
        return grant;
    }
}
