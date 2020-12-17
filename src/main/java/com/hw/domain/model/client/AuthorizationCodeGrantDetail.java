package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
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
public class AuthorizationCodeGrantDetail extends AbstractGrantDetail {
    public static final GrantType NAME = GrantType.AUTHORIZATION_CODE;

    @Convert(converter = StringSetConverter.class)
    private HashSet<String> redirectUrls = new HashSet<>();

    private boolean autoApprove = false;

    public AuthorizationCodeGrantDetail(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove, ClientId clientId) {
        super(grantTypes, clientId);
        setRedirectUrls(redirectUrls);
        setAutoApprove(autoApprove);
    }

    public Set<String> redirectUrls() {
        return redirectUrls;
    }

    public boolean autoApprove() {
        return autoApprove;
    }

    public void replace(@NotNull AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {
        if (grantTypeChanged(authorizationCodeGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        this.setRedirectUrls(authorizationCodeGrantDetail.redirectUrls());
        this.setAutoApprove(authorizationCodeGrantDetail.autoApprove());
        this.setEnabled(authorizationCodeGrantDetail.enabled());
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

}
