package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;

@Entity
@NoArgsConstructor
public class PasswordGrantDetail extends AbstractGrantDetail {
    public static final GrantType NAME = GrantType.PASSWORD;

    public PasswordGrantDetail(Set<GrantType> grantTypes, ClientId clientId, int accessTokenValiditySeconds) {
        super(grantTypes, clientId, accessTokenValiditySeconds);
    }

    public void replace(PasswordGrantDetail passwordGrantDetail) {
        if (grantTypeChanged(passwordGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        if (accessTokenValiditySecondsChanged(passwordGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId()));
        }
        this.setEnabled(passwordGrantDetail.enabled());
    }

}
