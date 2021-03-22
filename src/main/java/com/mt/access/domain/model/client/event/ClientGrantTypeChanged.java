package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientGrantTypeChanged extends ClientEvent{
    public ClientGrantTypeChanged(ClientId clientId) {
        super(clientId);
    }
}