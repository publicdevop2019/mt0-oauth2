package com.mt.identityaccess.domain.model.client;

import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.identityaccess.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.identityaccess.domain.model.client.event.ClientRefreshTokenChanged;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
public class RefreshTokenGrant  implements Serializable {
    public static final GrantType NAME = GrantType.REFRESH_TOKEN;
    @Column(name = "pwd_gt_refresh_token_gt_enabled")
    @Getter
    protected boolean enabled = false;

    @Column(name = "pwd_gt_refresh_token_gt_refresh_token_validity_seconds")
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int refreshTokenValiditySeconds = 0;

    public static void detectChange(RefreshTokenGrant a, RefreshTokenGrant b, ClientId clientId) {
        if (!ObjectUtils.equals(a, b)) {
            if (RefreshTokenGrant.grantTypeChanged(a, b)) {
                DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
            }
            if (RefreshTokenGrant.refreshTokenValiditySecondsChanged(a, b)) {
                DomainEventPublisher.instance().publish(new ClientRefreshTokenChanged(clientId));
            }
        }
    }

    protected void setEnabled(@Nullable Set<GrantType> grantTypes) {
        if (grantTypes == null)
            this.enabled = false;
        else
            enabled = grantTypes.stream().anyMatch(e -> e.equals(NAME));
    }

    public RefreshTokenGrant(Set<GrantType> grantTypes, int refreshTokenValiditySeconds) {
        setEnabled(grantTypes);
        setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    private static boolean grantTypeChanged(@Nullable RefreshTokenGrant a, @Nullable RefreshTokenGrant b) {
        if (a == null && b == null)
            return false;
        if (a == null)
            return true;
        if (b == null)
            return true;
        return a.isEnabled() != b.isEnabled();
    }

    private static boolean refreshTokenValiditySecondsChanged(@Nullable RefreshTokenGrant a, @Nullable RefreshTokenGrant b) {
        if (a == null && b == null)
            return false;
        if (a == null)
            return true;
        if (b == null)
            return true;
        return a.getRefreshTokenValiditySeconds() != b.getRefreshTokenValiditySeconds();
    }

}
