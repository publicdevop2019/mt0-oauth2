package com.hw.domain.model.client.event;

import com.hw.config.DomainEvent;
import com.hw.domain.model.client.ClientId;

import java.util.Date;

public class ClientAccessTokenValiditySecondsChanged implements DomainEvent {
    public ClientAccessTokenValiditySecondsChanged(ClientId p0) {
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