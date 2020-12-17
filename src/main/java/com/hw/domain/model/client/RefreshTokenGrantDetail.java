package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import com.hw.domain.model.client.event.ClientRefreshTokenChanged;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;

@Entity
@NoArgsConstructor
public class RefreshTokenGrantDetail extends AbstractGrantDetail {
    public static final GrantType NAME = GrantType.REFRESH_TOKEN;
    private int refreshTokenValiditySeconds = 0;

    public RefreshTokenGrantDetail(Set<GrantType> grantTypes, Integer refreshTokenValiditySeconds, ClientId clientId) {
        super(grantTypes, clientId);
        this.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    public int refreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void replace(RefreshTokenGrantDetail refreshTokenGrantDetail) {
        if (grantTypeChanged(refreshTokenGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        if (refreshTokenValiditySecondsChanged(refreshTokenGrantDetail.refreshTokenValiditySeconds())) {
            DomainEventPublisher.instance().publish(new ClientRefreshTokenChanged(clientId()));
        }
        this.setRefreshTokenValiditySeconds(refreshTokenGrantDetail.refreshTokenValiditySeconds());
        this.setEnabled(refreshTokenGrantDetail.enabled());
    }

    private boolean refreshTokenValiditySecondsChanged(int refreshTokenValiditySeconds) {
        return refreshTokenValiditySeconds() != refreshTokenValiditySeconds;
    }

    private void setRefreshTokenValiditySeconds(int tokenValiditySeconds) {
        this.refreshTokenValiditySeconds = tokenValiditySeconds;
    }

}
