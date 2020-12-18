package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
public class PasswordGrant extends AbstractGrant {

    private void setRefreshTokenGrant(RefreshTokenGrant refreshTokenGrant) {
        this.refreshTokenGrant = refreshTokenGrant;
    }

    @OneToOne(mappedBy = "passwordGrant", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true,optional = false)
    private RefreshTokenGrant refreshTokenGrant;

    public PasswordGrant(Set<GrantType> grantTypes, ClientId clientId, int accessTokenValiditySeconds, RefreshTokenGrant refreshTokenGrant) {
        super(grantTypes, clientId, accessTokenValiditySeconds);
        setRefreshTokenGrant(refreshTokenGrant);
    }

    public void replace(PasswordGrant passwordGrant) {
        if (grantTypeChanged(passwordGrant)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        if (accessTokenValiditySecondsChanged(passwordGrant)) {
            DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId()));
        }
        this.setEnabled(passwordGrant.enabled());
        if (refreshTokenGrant() != null)
            refreshTokenGrant().replace(passwordGrant.refreshTokenGrant());
    }

    public RefreshTokenGrant refreshTokenGrant() {
        return refreshTokenGrant;
    }

    @Override
    public GrantType name() {
        return GrantType.PASSWORD;
    }
}
