package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientAccessTokenChanged;

public class AccessTokenDetail {
    private int accessTokenValiditySeconds = 0;
    private transient ClientId clientId;

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

    public AccessTokenDetail(Integer accessTokenValiditySeconds, ClientId clientId) {
    }
}
