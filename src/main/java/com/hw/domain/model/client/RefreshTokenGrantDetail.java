package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import com.hw.domain.model.client.event.ClientRefreshTokenChanged;
import com.hw.shared.IdGenerator;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Set;

//@Entity
@NoArgsConstructor
public class RefreshTokenGrantDetail{
    public static final GrantType NAME = GrantType.REFRESH_TOKEN;
    @Id
    protected long id;

    protected boolean enabled = false;

//    @Embedded
    @Transient
    protected ClientId clientId;

    private int refreshTokenValiditySeconds = 0;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private PasswordGrantDetail passwordGrantDetail;

    public boolean enabled() {
        return enabled;
    }

    public ClientId clientId() {
        return clientId;
    }

    protected void setClientId(@Nullable ClientId clientId) {
        this.clientId = clientId;
    }

    protected void setEnabled(@Nullable Set<GrantType> grantTypes) {
        if (grantTypes == null)
            this.enabled = false;
        else
            enabled = grantTypes.stream().anyMatch(e -> e.equals(NAME));
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RefreshTokenGrantDetail(Set<GrantType> grantTypes, ClientId clientId, int refreshTokenValiditySeconds) {
        this.id = IdGenerator.instance().id();
        setEnabled(grantTypes);
        setClientId(clientId);
        setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    public void internalOnlySetPasswordGrantDetail(PasswordGrantDetail passwordGrantDetail) {
        this.passwordGrantDetail = passwordGrantDetail;
    }

    protected boolean grantTypeChanged(RefreshTokenGrantDetail refreshTokenGrantDetail) {
        return enabled != refreshTokenGrantDetail.enabled();
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
