package com.mt.identityaccess.domain.model.client.grant;

import com.hw.config.DomainEventPublisher;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.GrantType;
import com.mt.identityaccess.domain.model.client.event.ClientGrantTypeChanged;

import java.util.Set;

public class PasswordGrantDetail {
    private GrantType grantType;
    private ClientId clientId;

    public GrantType getGrantType() {
        return grantType;
    }

    public PasswordGrantDetail(Set<GrantType> grantTypes) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.PASSWORD)).findFirst().orElse(null));
    }

    private void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    public void replace(PasswordGrantDetail passwordGrantDetail) {
        if (grantTypeChanged(passwordGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
        }
        this.setGrantType(passwordGrantDetail.grantType);
    }

    private boolean grantTypeChanged(PasswordGrantDetail passwordGrantDetail) {
        return !grantType.equals(passwordGrantDetail.grantType);
    }
}
