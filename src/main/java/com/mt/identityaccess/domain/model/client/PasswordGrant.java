package com.mt.identityaccess.domain.model.client;

import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import java.util.Set;

@NoArgsConstructor
public class PasswordGrant extends AbstractGrant {

    private void setRefreshTokenGrant(RefreshTokenGrant refreshTokenGrant) {
        this.refreshTokenGrant = refreshTokenGrant;
    }

    @Embedded
    private RefreshTokenGrant refreshTokenGrant;

    public PasswordGrant(Set<GrantType> grantTypes, int accessTokenValiditySeconds, RefreshTokenGrant refreshTokenGrant) {
        super(grantTypes, accessTokenValiditySeconds);
        setRefreshTokenGrant(refreshTokenGrant);
    }

    public void replace(PasswordGrant passwordGrant) {
//        if (grantTypeChanged(passwordGrant)) {
//            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
//        }
//        if (accessTokenValiditySecondsChanged(passwordGrant)) {
//            DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId()));
//        }
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
