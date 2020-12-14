package com.mt.identityaccess.domain.model.client.grant;

import com.hw.config.DomainEventPublisher;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.event.ClientAccessTokenChanged;

public class AccessTokenDetail {
    private int accessTokenValiditySeconds = 0;
    private ClientId clientId;

    public AccessTokenDetail(Integer accessTokenValiditySeconds) {
        this.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public void replace(AccessTokenDetail accessTokenDetail) {
        if (accessTokenChanged(accessTokenDetail)) {
            DomainEventPublisher.instance().publish(new ClientAccessTokenChanged(clientId));
        }
        accessTokenValiditySeconds = accessTokenDetail.accessTokenValiditySeconds;
    }

    private boolean accessTokenChanged(AccessTokenDetail accessTokenDetail) {
        return accessTokenValiditySeconds != accessTokenDetail.accessTokenValiditySeconds;
    }
}
