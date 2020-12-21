package com.mt.identityaccess.domain.model.client;

import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
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

    protected boolean grantTypeChanged(AbstractGrant abstractGrant) {
        return enabled != abstractGrant.enabled();
    }

    protected boolean accessTokenValiditySecondsChanged(AbstractGrant abstractGrant) {
        return accessTokenValiditySeconds() != abstractGrant.accessTokenValiditySeconds();
    }

    private void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }
}
