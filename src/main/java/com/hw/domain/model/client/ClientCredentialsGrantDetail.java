package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;

@Entity
@NoArgsConstructor
public class ClientCredentialsGrantDetail extends AbstractGrantDetail {
    public static final GrantType NAME = GrantType.CLIENT_CREDENTIALS;

    public ClientCredentialsGrantDetail(Set<GrantType> grantTypes, ClientId clientId) {
        super(grantTypes, clientId);
    }

    public void replace(ClientCredentialsGrantDetail clientCredentialsGrantDetail) {
        if (grantTypeChanged(clientCredentialsGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId()));
        }
        this.setEnabled(clientCredentialsGrantDetail.enabled());
    }
}
