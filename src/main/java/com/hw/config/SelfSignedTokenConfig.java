package com.hw.config;

import com.mt.identityaccess.domain.model.app.AppBizClientApplicationService;
import com.mt.identityaccess.application.representation.AppBizClientRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Component;

import static com.mt.identityaccess.domain.model.app.GrantTypeEnum.CLIENT_CREDENTIALS;

/**
 * this class is only for authentication server itself
 */
@Component
public class SelfSignedTokenConfig {
    @Value("${security.oauth2.client.clientId:#{null}}")
    private Long clientId;
    @Autowired
    private AppBizClientApplicationService appBizClientApplicationService;
    private TokenGranter tokenGranter;

    public void setTokenGranter(TokenGranter tokenGranter) {
        this.tokenGranter = tokenGranter;
    }

    public OAuth2AccessToken getSelfSignedAccessToken() {
        AppBizClientRep appBizClientRep = appBizClientApplicationService.readById(clientId);
        TokenRequest tokenRequest = new TokenRequest(null, appBizClientRep.getClientId(), appBizClientRep.getScope(), CLIENT_CREDENTIALS.name().toLowerCase());
        return tokenGranter.grant(CLIENT_CREDENTIALS.name().toLowerCase(), tokenRequest);
    }
}
