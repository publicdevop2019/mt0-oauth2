package com.mt.identityaccess.domain.model.client;

import java.util.Set;

public class RefreshTokenGrantDetail {
    private GrantType grantType;

    public GrantType getGrantType() {
        return grantType;
    }

    private Integer refreshTokenValiditySeconds;

    public RefreshTokenGrantDetail(Set<GrantType> grantTypes, Integer refreshTokenValiditySeconds) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.REFRESH_TOKEN)).findFirst().orElse(null));
        this.setAccessTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    private void setAccessTokenValiditySeconds(Integer tokenValiditySeconds) {
        if (grantType != null)
            this.refreshTokenValiditySeconds = tokenValiditySeconds;
    }

    private void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }
}
