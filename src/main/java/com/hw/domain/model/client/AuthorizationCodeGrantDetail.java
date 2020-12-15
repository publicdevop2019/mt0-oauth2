package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;

import java.util.HashSet;
import java.util.Set;

public class AuthorizationCodeGrantDetail {
    public GrantType getGrantType() {
        return grantType;
    }

    public AuthorizationCodeGrantDetail(Set<GrantType> grantTypeEnums, Set<String> registeredRedirectUri, boolean autoApprove, ClientId clientId) {
    }

    private transient GrantType grantType;
    //    @Convert(converter = StringSetConverter.class)
    private HashSet<String> redirectUrls = new HashSet<>();
    private boolean autoApprove = false;
    private transient ClientId clientId;

    public AuthorizationCodeGrantDetail(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.AUTHORIZATION_CODE)).findFirst().orElse(null));
        this.setRedirectUrls(redirectUrls);
        this.setAutoApprove(autoApprove);
    }

    public void setAutoApprove(boolean autoApprove) {
        if (GrantType.AUTHORIZATION_CODE.equals(grantType))
            this.autoApprove = autoApprove;
    }

    public Set<String> redirectUrls() {
        return redirectUrls;
    }

    public boolean autoApprove() {
        return autoApprove;
    }

    public void setRedirectUrls(Set<String> redirectUrls) {
        if (GrantType.AUTHORIZATION_CODE.equals(grantType))
            this.redirectUrls = new HashSet<>(redirectUrls);
    }

    public void setGrantType(GrantType grantType) {
        if (!GrantType.AUTHORIZATION_CODE.equals(grantType)) {
            this.redirectUrls = null;
        }
        this.grantType = grantType;
    }

    public void replace(AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {
        if (grantTypeChanged(authorizationCodeGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
        }
        this.setAutoApprove(authorizationCodeGrantDetail.autoApprove);
        this.setGrantType(authorizationCodeGrantDetail.grantType);
        this.setRedirectUrls(authorizationCodeGrantDetail.redirectUrls);

    }

    private boolean grantTypeChanged(AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {
        return !grantType.equals(authorizationCodeGrantDetail.grantType);
    }
}
