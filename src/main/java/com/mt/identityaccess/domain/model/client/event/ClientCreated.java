package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientCreated extends ClientEvent {
    public ClientCreated(ClientId clientId) {
        super(clientId);
    }
}
