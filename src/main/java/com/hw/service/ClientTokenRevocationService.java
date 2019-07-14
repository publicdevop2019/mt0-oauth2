package com.hw.service;

import com.hw.entity.Client;
import com.hw.interfaze.TokenRevocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

@Component
public class ClientTokenRevocationService implements TokenRevocationService<Client> {

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Value("${url.zuul.client}")
    private String url;

    @Value("${feature.token.revocation}")
    private Boolean enabled;

    /**
     * include : clientId, secret, authority, scope, access token validity sec, refresh token validity sec, grant type, resource ids,
     * redirect url
     * exclude : resource indicator
     *
     * @param oldClient
     * @param newClient
     * @return
     */
    @Override
    public boolean shouldRevoke(Client oldClient, Client newClient) {
        if (!enabled) {
            return false;
        } else if (!newClient.getClientId().equals(oldClient.getClientId())) {
            return true;
        } else if (StringUtils.hasText(newClient.getClientSecret())) {
            return true;
        } else if (authorityChanged(oldClient, newClient)) {
            return true;
        } else if (scopeChanged(oldClient, newClient)) {
            return true;
        } else if (accessTokenChanged(oldClient, newClient)) {
            return true;
        } else if (refreshTokenChanged(oldClient, newClient)) {
            return true;
        } else if (grantTypeChanged(oldClient, newClient)) {
            return true;
        } else if (resourceIdChanged(oldClient, newClient)) {
            return true;
        } else if (redirectUrlChanged(oldClient, newClient)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void blacklist(String name, boolean shouldRevoke) {
        if (shouldRevoke && enabled) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("name", name);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> hashMapHttpEntity = new HttpEntity<>(map, headers);
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity, String.class);
        }
    }

    private boolean authorityChanged(Client oldClient, Client newClient) {
        return !oldClient.getGrantedAuthorities().equals(newClient.getGrantedAuthorities());
    }

    private boolean scopeChanged(Client oldClient, Client newClient) {
        return !oldClient.getScopeEnums().equals(newClient.getScopeEnums());
    }

    /**
     * access token validity seconds can not be null
     *
     * @param oldClient
     * @param newClient
     * @return
     */
    private boolean accessTokenChanged(Client oldClient, Client newClient) {
        return !oldClient.getAccessTokenValiditySeconds().equals(newClient.getAccessTokenValiditySeconds());
    }

    private boolean refreshTokenChanged(Client oldClient, Client newClient) {
        if (oldClient.getRefreshTokenValiditySeconds() == null && newClient.getRefreshTokenValiditySeconds() == null) {
            return false;
        } else if (oldClient.getRefreshTokenValiditySeconds() != null && oldClient.getRefreshTokenValiditySeconds().equals(newClient.getRefreshTokenValiditySeconds())) {
            return false;
        } else if (newClient.getRefreshTokenValiditySeconds() != null && newClient.getRefreshTokenValiditySeconds().equals(oldClient.getRefreshTokenValiditySeconds())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean grantTypeChanged(Client oldClient, Client newClient) {
        return !oldClient.getGrantTypeEnums().equals(newClient.getGrantTypeEnums());
    }

    private boolean redirectUrlChanged(Client oldClient, Client newClient) {
        if ((oldClient.getRegisteredRedirectUri() == null || oldClient.getRegisteredRedirectUri().isEmpty())
                && (newClient.getRegisteredRedirectUri() == null || newClient.getRegisteredRedirectUri().isEmpty())) {
            return false;
        } else if (oldClient.getRegisteredRedirectUri() != null && oldClient.getRegisteredRedirectUri().equals(newClient.getRegisteredRedirectUri())) {
            return false;
        } else if (newClient.getRegisteredRedirectUri() != null && newClient.getRegisteredRedirectUri().equals(oldClient.getRegisteredRedirectUri())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean resourceIdChanged(Client oldClient, Client newClient) {
        return !oldClient.getResourceIds().equals(newClient.getResourceIds());
    }

}
