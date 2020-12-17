package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;

@Entity
@NoArgsConstructor
public class ClientCredentialsGrantDetail extends AbstractGrantDetail {
    public static final GrantType NAME = GrantType.CLIENT_CREDENTIALS;

    public ClientCredentialsGrantDetail(Set<GrantType> grantTypes, ClientId clientId, int accessTokenValiditySeconds) {
        super(grantTypes, clientId, accessTokenValiditySeconds);
    }

    public void replace(ClientCredentialsGrantDetail clientCredentialsGrantDetail) {
        if (grantTypeChanged(clientCredentialsGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        if (accessTokenValiditySecondsChanged(clientCredentialsGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId()));
        }
        this.setEnabled(clientCredentialsGrantDetail.enabled());
    }
}
