package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientAccessibilityRemoved extends ClientEvent {
    public ClientAccessibilityRemoved(ClientId clientId) {
        super(clientId);
    }
}
