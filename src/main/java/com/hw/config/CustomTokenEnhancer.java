package com.hw.config;

import com.mt.identityaccess.domain.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * capture issued at time to enable token revocation feature,
 * use user id instead of username to enhance security
 */
@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    UserRepository resourceOwnerRepo;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Map<String, Object> info = new HashMap<>();
        info.put("iat", Instant.now().getEpochSecond());
        if (!authentication.isClientOnly()) {
            long l = Long.parseLong(authentication.getName());
            info.put("uid", Long.toString(l));
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
