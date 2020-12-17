package com.hw.domain.model.client;

import com.hw.shared.IdGenerator;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractGrantDetail {
    public static GrantType NAME;
    @Id
    protected long id;

    public boolean enabled() {
        return enabled;
    }

    protected boolean enabled = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Client client;

    @Embedded
    protected ClientId clientId;

    public ClientId clientId() {
        return clientId;
    }

    protected void setClientId(@Nullable ClientId clientId) {
        this.clientId = clientId;
    }

    protected void setEnabled(@Nullable Set<GrantType> grantTypes) {
        if (grantTypes == null)
            this.enabled = false;
        else
            enabled = grantTypes.stream().anyMatch(e -> e.equals(NAME));
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AbstractGrantDetail(Set<GrantType> grantTypes, ClientId clientId) {
        this.id = IdGenerator.instance().id();
        setEnabled(grantTypes);
        setClientId(clientId);
    }

    private void setClient(Client client) {
        this.client = client;
    }

    protected void internalOnlySetClient(Client client) {
        this.client = client;
    }

    protected boolean grantTypeChanged(AbstractGrantDetail abstractGrantDetail) {
        return enabled != abstractGrantDetail.enabled();
    }
}
