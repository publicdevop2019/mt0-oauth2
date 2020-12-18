package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import com.hw.shared.StringSetConverter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class AuthorizationCodeGrant extends AbstractGrant {

    @Convert(converter = StringSetConverter.class)
    private HashSet<String> redirectUrls = new HashSet<>();

    private boolean autoApprove = false;

    public AuthorizationCodeGrant(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove, ClientId clientId, int accessTokenValiditySeconds) {
        super(grantTypes, clientId, accessTokenValiditySeconds);
        setRedirectUrls(redirectUrls);
        setAutoApprove(autoApprove);
    }

    public Set<String> redirectUrls() {
        return redirectUrls;
    }

    public boolean autoApprove() {
        return autoApprove;
    }

    public void replace(@NotNull AuthorizationCodeGrant authorizationCodeGrant) {
        if (grantTypeChanged(authorizationCodeGrant)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        if (accessTokenValiditySecondsChanged(authorizationCodeGrant)) {
            DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId()));
        }
        this.setRedirectUrls(authorizationCodeGrant.redirectUrls());
        this.setAutoApprove(authorizationCodeGrant.autoApprove());
        this.setEnabled(authorizationCodeGrant.enabled());
    }

    private void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    private void setRedirectUrls(@Nullable Set<String> redirectUrls) {
        if (redirectUrls == null) {
            this.redirectUrls = new HashSet<>();
        } else {
            this.redirectUrls = new HashSet<>(redirectUrls);
        }
    }

    @Override
    public GrantType name() {
        return GrantType.AUTHORIZATION_CODE;
    }
}
