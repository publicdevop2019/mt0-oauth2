package com.mt.identityaccess.infrastructure.service;

import com.mt.identityaccess.application.ApplicationServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Component;

import static com.mt.identityaccess.domain.model.client.GrantType.CLIENT_CREDENTIALS;

/**
 * this class is only for authentication server itself
 */
@Component
public class SelfSignedJwtTokenService {
    @Value("${security.oauth2.client.clientId:#{null}}")
    private String id;

    private TokenGranter tokenGranter;

    public void setTokenGranter(TokenGranter tokenGranter) {
        this.tokenGranter = tokenGranter;
    }

    public OAuth2AccessToken getSelfSignedAccessToken() {
        ClientDetails clientDetails = ApplicationServiceRegistry.clientApplicationService().loadClientByClientId(id);
        TokenRequest tokenRequest = new TokenRequest(null, clientDetails.getClientId(), clientDetails.getScope(), CLIENT_CREDENTIALS.name().toLowerCase());
        return tokenGranter.grant(CLIENT_CREDENTIALS.name().toLowerCase(), tokenRequest);
    }
}
