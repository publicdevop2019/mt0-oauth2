package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientAccessTokenValiditySecondsChanged;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;

@Entity
@NoArgsConstructor
public class ClientCredentialsGrant extends AbstractGrant {

    @Override
    public GrantType name() {
        return GrantType.CLIENT_CREDENTIALS;
    }

    public ClientCredentialsGrant(Set<GrantType> grantTypes, ClientId clientId, int accessTokenValiditySeconds) {
        super(grantTypes, clientId, accessTokenValiditySeconds);
    }

    public void replace(ClientCredentialsGrant clientCredentialsGrant) {
        if (grantTypeChanged(clientCredentialsGrant)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        if (accessTokenValiditySecondsChanged(clientCredentialsGrant)) {
            DomainEventPublisher.instance().publish(new ClientAccessTokenValiditySecondsChanged(clientId()));
        }
        this.setEnabled(clientCredentialsGrant.enabled());
    }
}
