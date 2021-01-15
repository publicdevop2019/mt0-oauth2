package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientAccessibilityRemoved extends ClientEvent {
    public ClientAccessibilityRemoved(ClientId clientId) {
        super(clientId);
    }
}
