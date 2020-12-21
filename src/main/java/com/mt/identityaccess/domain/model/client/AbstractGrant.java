package com.mt.identityaccess.domain.model.client;

import com.google.common.base.Objects;
import com.mt.identityaccess.config.DomainEventPublisher;
import com.mt.identityaccess.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.mt.identityaccess.domain.model.client.event.ClientGrantTypeChanged;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractGrant implements Serializable {
    public abstract GrantType name();

    protected boolean enabled = false;

    private int accessTokenValiditySeconds = 0;

    public boolean enabled() {
        return enabled;
    }

    public int accessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    protected void setEnabled(@Nullable Set<GrantType> grantTypes) {
        if (grantTypes == null)
            this.enabled = false;
        else
            enabled = grantTypes.stream().anyMatch(e -> e.equals(name()));
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AbstractGrant(Set<GrantType> grantTypes, int accessTokenValiditySeconds) {
        this();
        setEnabled(grantTypes);
        setAccessTokenValiditySeconds(accessTokenValiditySeconds);
    }

    private static boolean grantTypeChanged(@Nullable AbstractGrant a, @Nullable AbstractGrant b) {
        if (a == null && b == null)
            return false;
        if (a == null)
            return true;
        if (b == null)
            return true;
        return a.enabled() != b.enabled();
    }

    private static boolean accessTokenValiditySecondsChanged(@Nullable AbstractGrant a, @Nullable AbstractGrant b) {
        if (a == null && b == null)
            return false;
        if (a == null)
            return true;
        if (b == null)
            return true;
        return a.accessTokenValiditySeconds() != b.accessTokenValiditySeconds();
    }

    public static void detectChange(@Nullable AbstractGrant a, @Nullable AbstractGrant b, ClientId clientId) {
        if (!ObjectUtils.equals(a, b)) {
            if (AbstractGrant.grantTypeChanged(a, b)) {
                DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
            }
            if (AbstractGrant.accessTokenValiditySecondsChanged(a, b)) {
                DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId));
            }
        }
    }

    private void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractGrant)) return false;
        AbstractGrant that = (AbstractGrant) o;
        return enabled == that.enabled && accessTokenValiditySeconds == that.accessTokenValiditySeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(enabled, accessTokenValiditySeconds);
    }
}
