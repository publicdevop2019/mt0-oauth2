package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientRefreshTokenChanged extends ClientEvent{
    public ClientRefreshTokenChanged(ClientId clientId) {
        super(clientId);
    }
}