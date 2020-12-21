package com.mt.identityaccess.domain.model.client;

import com.mt.identityaccess.config.DomainEventPublisher;
import com.mt.identityaccess.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.identityaccess.domain.model.client.event.ClientRefreshTokenChanged;
import com.mt.common.snowflake.IdGenerator;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Set;

//@Entity
@NoArgsConstructor
public class RefreshTokenGrant {
    public static final GrantType NAME = GrantType.REFRESH_TOKEN;
//    @Id
//    protected long id;
    @Column(name = "pwd_gt_refresh_token_gt_enabled")
    protected boolean enabled = false;

//    @Embedded
//    protected ClientId clientId;

    @Column(name = "pwd_gt_refresh_token_gt_refresh_token_validity_seconds")
    private int refreshTokenValiditySeconds = 0;

//    @MapsId
//    @OneToOne
//    @JoinColumn(name = "id")
//    private PasswordGrant passwordGrant;

    public boolean enabled() {
        return enabled;
    }

//    public ClientId clientId() {
//        return clientId;
//    }
//
//    protected void setClientId(@Nullable ClientId clientId) {
//        this.clientId = clientId;
//    }

    protected void setEnabled(@Nullable Set<GrantType> grantTypes) {
        if (grantTypes == null)
            this.enabled = false;
        else
            enabled = grantTypes.stream().anyMatch(e -> e.equals(NAME));
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RefreshTokenGrant(Set<GrantType> grantTypes, ClientId clientId, int refreshTokenValiditySeconds) {
        setEnabled(grantTypes);
//        setClientId(clientId);
        setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

//    public void internalOnlySetPasswordGrant(PasswordGrant passwordGrant) {
//        this.passwordGrant = passwordGrant;
//    }

    protected boolean grantTypeChanged(RefreshTokenGrant refreshTokenGrant) {
        return enabled != refreshTokenGrant.enabled();
    }

    public int refreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void replace(RefreshTokenGrant refreshTokenGrant) {
//        if (grantTypeChanged(refreshTokenGrant)) {
//            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
//        }
//        if (refreshTokenValiditySecondsChanged(refreshTokenGrant.refreshTokenValiditySeconds())) {
//            DomainEventPublisher.instance().publish(new ClientRefreshTokenChanged(clientId()));
//        }
        this.setRefreshTokenValiditySeconds(refreshTokenGrant.refreshTokenValiditySeconds());
        this.setEnabled(refreshTokenGrant.enabled());
    }

    private boolean refreshTokenValiditySecondsChanged(int refreshTokenValiditySeconds) {
        return refreshTokenValiditySeconds() != refreshTokenValiditySeconds;
    }

    private void setRefreshTokenValiditySeconds(int tokenValiditySeconds) {
        this.refreshTokenValiditySeconds = tokenValiditySeconds;
    }

}
