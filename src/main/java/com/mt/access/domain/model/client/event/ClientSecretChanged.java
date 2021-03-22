package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientSecretChanged extends ClientEvent {
    public ClientSecretChanged(ClientId clientId) {
        super(clientId);
    }
}