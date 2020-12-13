package com.mt.identityaccess.domain.model.client;

public class AccessTokenDetail {
    private int accessTokenValiditySeconds = 0;

    public AccessTokenDetail(Integer accessTokenValiditySeconds) {
        this.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }
}
