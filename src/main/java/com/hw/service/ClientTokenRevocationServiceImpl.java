package com.hw.service;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.entity.Client;
import com.hw.interfaze.TokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;

@Service
public class ClientTokenRevocationServiceImpl extends CommonTokenRevocationService implements TokenRevocationService<Client> {

    @Value("${url.zuul.client}")
    private String url;

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
        if (!newClient.getClientId().equals(oldClient.getClientId())) {
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
        blacklist(url, name, shouldRevoke);
    }

    private boolean authorityChanged(Client oldClient, Client newClient) {
        HashSet<GrantedAuthorityImpl<ClientAuthorityEnum>> grantedAuthorities = new HashSet<>(oldClient.getGrantedAuthorities());
        HashSet<GrantedAuthorityImpl<ClientAuthorityEnum>> grantedAuthorities2 = new HashSet<>(newClient.getGrantedAuthorities());
        return !grantedAuthorities.equals(grantedAuthorities2);
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
