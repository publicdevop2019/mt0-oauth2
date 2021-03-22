package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientAccessTokenValiditySecondsChanged extends ClientEvent {
    public ClientAccessTokenValiditySecondsChanged(ClientId clientId) {
        super(clientId);
    }
}
