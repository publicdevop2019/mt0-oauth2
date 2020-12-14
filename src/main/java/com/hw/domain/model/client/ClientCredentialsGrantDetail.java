package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.DomainRegistry;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import com.hw.domain.model.client.event.ClientSecretChanged;
import org.springframework.util.StringUtils;

import java.util.Set;

public class ClientCredentialsGrantDetail {
    private transient GrantType grantType;
    private String secret;
    private transient ClientId clientId;

    public GrantType getGrantType() {
        return grantType;
    }

    public ClientCredentialsGrantDetail(Set<GrantType> grantTypes, String secret) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.CLIENT_CREDENTIALS)).findFirst().orElse(null));
        this.setSecret(secret);
    }

    public ClientCredentialsGrantDetail(Set<GrantType> grantTypes) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.CLIENT_CREDENTIALS)).findFirst().orElse(null));
    }

    private void setGrantType(GrantType grantType) {
        if (GrantType.CLIENT_CREDENTIALS.equals(grantType))
            this.grantType = grantType;
    }

    public void setSecret(String secret) {
        if (GrantType.CLIENT_CREDENTIALS.equals(grantType))
            this.secret = DomainRegistry.encryptionService().encryptedValue(secret);
    }

    public void replace(ClientCredentialsGrantDetail clientCredentialsGrantDetail) {
        if (grantTypeChanged(clientCredentialsGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
        }
        if (secretChanged(clientCredentialsGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientSecretChanged(clientId));
        }
        setGrantType(clientCredentialsGrantDetail.grantType);
        setSecret(clientCredentialsGrantDetail.secret);
    }

    private boolean grantTypeChanged(ClientCredentialsGrantDetail clientCredentialsGrantDetail) {
        return !grantType.equals(clientCredentialsGrantDetail.grantType);
    }

    private boolean secretChanged(ClientCredentialsGrantDetail clientCredentialsGrantDetail) {
        return StringUtils.hasText(clientCredentialsGrantDetail.secret);
    }
}
