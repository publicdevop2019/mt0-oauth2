package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientAccessibleChanged extends ClientEvent {
    public ClientAccessibleChanged(ClientId clientId) {
        super(clientId);
    }
}
