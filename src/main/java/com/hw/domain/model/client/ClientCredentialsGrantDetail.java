package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import com.hw.shared.IdGenerator;

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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;


    public ClientCredentialsGrantDetail(Set<GrantType> grantTypes, ClientId clientId) {
        enabled = grantTypes.stream().anyMatch(e -> e.equals(GrantType.CLIENT_CREDENTIALS));
        id = IdGenerator.instance().id();
    }

    public ClientCredentialsGrantDetail() {
    }

    public void replace(ClientCredentialsGrantDetail clientCredentialsGrantDetail) {
        if (enabled() != clientCredentialsGrantDetail.enabled()) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(client.clientId()));
        }
    }
}
