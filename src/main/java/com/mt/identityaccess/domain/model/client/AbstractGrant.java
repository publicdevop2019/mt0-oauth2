package com.mt.identityaccess.domain.model.client;

import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.identityaccess.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.mt.identityaccess.domain.model.client.event.ClientGrantTypeChanged;
import lombok.*;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode
public abstract class AbstractGrant implements Serializable {
    public abstract GrantType name();

    @Getter
    protected boolean enabled = false;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private int accessTokenValiditySeconds = 0;

    public AbstractGrant(Set<GrantType> grantTypes, int accessTokenValiditySeconds) {
        this();
        setEnabled(grantTypes);
        setAccessTokenValiditySeconds(accessTokenValiditySeconds);
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

    private void setEnabled(@Nullable Set<GrantType> grantTypes) {
        if (grantTypes == null)
            this.enabled = false;
        else
            enabled = grantTypes.stream().anyMatch(e -> e.equals(name()));
    }

    private static boolean grantTypeChanged(@Nullable AbstractGrant a, @Nullable AbstractGrant b) {
        if (a == null && b == null)
            return false;
        if (a == null)
            return true;
        if (b == null)
            return true;
        return a.isEnabled() != b.isEnabled();
    }

    private static boolean accessTokenValiditySecondsChanged(@Nullable AbstractGrant a, @Nullable AbstractGrant b) {
        if (a == null && b == null)
            return false;
        if (a == null)
            return true;
        if (b == null)
            return true;
        return a.getAccessTokenValiditySeconds() != b.getAccessTokenValiditySeconds();
    }

}
