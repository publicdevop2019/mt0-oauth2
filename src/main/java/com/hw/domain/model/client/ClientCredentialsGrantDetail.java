package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ClientCredentialsGrantDetail {
    public static final GrantType NAME = GrantType.CLIENT_CREDENTIALS;
    @Id
    private long id;

    public boolean enabled() {
        return enabled;
    }

    private boolean enabled;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Client client;

    @Embedded
    private ClientId clientId;

    public ClientId clientId() {
        return clientId;
    }

    public ClientCredentialsGrantDetail(Set<GrantType> grantTypes, ClientId clientId) {
        enabled = grantTypes.stream().anyMatch(e -> e.equals(NAME));
        this.clientId = clientId;
    }

    public ClientCredentialsGrantDetail() {
    }

    public void replace(ClientCredentialsGrantDetail clientCredentialsGrantDetail) {
        if (enabled() != clientCredentialsGrantDetail.enabled()) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(new ClientId("")));
        }
    }
}
