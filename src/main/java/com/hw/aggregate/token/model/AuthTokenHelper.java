package com.hw.aggregate.token.model;

import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.BizClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * this class is only for authentication server itself
 */
@Component
public class AuthTokenHelper {

    @Autowired
    private BizClientRepo clientRepo;

    public void setTokenGranter(TokenGranter tokenGranter) {
        this.tokenGranter = tokenGranter;
    }

    private TokenGranter tokenGranter;


    private String CLIENT_CREDENTIALS = "client_credentials";

    public OAuth2AccessToken getSelfSignedAccessToken() {
        Optional<BizClient> byId = clientRepo.findByClientId("oauth2-id");
        if (byId.isEmpty())
            throw new IllegalArgumentException("root authorization client not found!,this should never happen");
        BizClient client = byId.get();
        TokenRequest tokenRequest = new TokenRequest(null, client.getClientId(), client.getScope(), CLIENT_CREDENTIALS);
        OAuth2AccessToken grant = tokenGranter.grant(CLIENT_CREDENTIALS, tokenRequest);
        return grant;
    }
}
