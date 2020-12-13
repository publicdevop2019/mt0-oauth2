package com.mt.identityaccess.infrastructure.service;

import com.mt.identityaccess.application.deprecated.AppBizClientApplicationService;
import com.mt.identityaccess.application.representation.ClientDetailsRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
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
    private Long clientId;
    @Autowired
    private AppBizClientApplicationService appBizClientApplicationService;
    private TokenGranter tokenGranter;

    public void setTokenGranter(TokenGranter tokenGranter) {
        this.tokenGranter = tokenGranter;
    }

    public OAuth2AccessToken getSelfSignedAccessToken() {
        ClientDetailsRepresentation appBizClientRep = appBizClientApplicationService.readById(clientId);
        TokenRequest tokenRequest = new TokenRequest(null, appBizClientRep.getClientId(), appBizClientRep.getScope(), CLIENT_CREDENTIALS.name().toLowerCase());
        return tokenGranter.grant(CLIENT_CREDENTIALS.name().toLowerCase(), tokenRequest);
    }
}
