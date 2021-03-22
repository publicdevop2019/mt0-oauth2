package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientRefreshTokenChanged extends ClientEvent{
    public ClientRefreshTokenChanged(ClientId clientId) {
        super(clientId);
    }
}