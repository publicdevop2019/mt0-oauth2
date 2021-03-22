package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientCreated extends ClientEvent {
    public ClientCreated(ClientId clientId) {
        super(clientId);
    }
}
