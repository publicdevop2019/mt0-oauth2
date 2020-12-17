package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
public class PasswordGrantDetail extends AbstractGrantDetail {
    public static final GrantType NAME = GrantType.PASSWORD;

    private void setRefreshTokenGrantDetail(RefreshTokenGrantDetail refreshTokenGrantDetail) {
        this.refreshTokenGrantDetail = refreshTokenGrantDetail;
    }

    @OneToOne(mappedBy = "passwordGrantDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private RefreshTokenGrantDetail refreshTokenGrantDetail;

    public PasswordGrantDetail(Set<GrantType> grantTypes, ClientId clientId, int accessTokenValiditySeconds, RefreshTokenGrantDetail refreshTokenGrantDetail) {
        super(grantTypes, clientId, accessTokenValiditySeconds);
        setRefreshTokenGrantDetail(refreshTokenGrantDetail);
    }

    public void replace(PasswordGrantDetail passwordGrantDetail) {
        if (grantTypeChanged(passwordGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        if (accessTokenValiditySecondsChanged(passwordGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId()));
        }
        this.setEnabled(passwordGrantDetail.enabled());
        if (refreshTokenGrantDetail() != null)
            refreshTokenGrantDetail().replace(passwordGrantDetail.refreshTokenGrantDetail());
    }

    public RefreshTokenGrantDetail refreshTokenGrantDetail() {
        return refreshTokenGrantDetail;
    }
}
