package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientAccessTokenValiditySecondsChanged extends ClientEvent {
    public ClientAccessTokenValiditySecondsChanged(ClientId clientId) {
        super(clientId);
    }
}
