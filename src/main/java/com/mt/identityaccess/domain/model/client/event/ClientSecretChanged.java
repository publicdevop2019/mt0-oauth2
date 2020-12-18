package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.config.DomainEvent;
import com.mt.identityaccess.domain.model.client.ClientId;

import java.util.Date;

public class ClientSecretChanged implements DomainEvent {
    public ClientSecretChanged(ClientId clientId) {
    }

    @Override
    public int eventVersion() {
        return 0;
    }

    @Override
    public Date occurredOn() {
        return null;
    }
}
