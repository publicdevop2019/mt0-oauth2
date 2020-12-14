package com.mt.identityaccess.domain.model.client.grant;

import com.hw.config.DomainEventPublisher;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.GrantType;
import com.mt.identityaccess.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.identityaccess.domain.model.client.event.ClientRefreshTokenChanged;

import java.util.Set;

public class RefreshTokenGrantDetail {
    private GrantType grantType;
    private ClientId clientId;

    public GrantType getGrantType() {
        return grantType;
    }

    private Integer refreshTokenValiditySeconds;

    public Integer refreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public RefreshTokenGrantDetail(Set<GrantType> grantTypes, Integer refreshTokenValiditySeconds) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.REFRESH_TOKEN)).findFirst().orElse(null));
        this.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    private void setRefreshTokenValiditySeconds(Integer tokenValiditySeconds) {
        if (grantType != null)
            this.refreshTokenValiditySeconds = tokenValiditySeconds;
    }

    private void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    public void replace(RefreshTokenGrantDetail refreshTokenGrantDetail) {
        if (grantTypeChanged(refreshTokenGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
        }
        if (refreshTokenValiditySecondsChanged(refreshTokenGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientRefreshTokenChanged(clientId));
        }
        this.setGrantType(refreshTokenGrantDetail.grantType);
        this.setRefreshTokenValiditySeconds(refreshTokenGrantDetail.refreshTokenValiditySeconds);
    }

    private boolean grantTypeChanged(RefreshTokenGrantDetail passwordGrantDetail) {
        return !grantType.equals(passwordGrantDetail.grantType);
    }

    private boolean refreshTokenValiditySecondsChanged(RefreshTokenGrantDetail passwordGrantDetail) {
        return !refreshTokenValiditySeconds.equals(passwordGrantDetail.refreshTokenValiditySeconds);
    }
}
