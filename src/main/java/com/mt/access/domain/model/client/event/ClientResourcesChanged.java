package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientResourcesChanged extends ClientEvent{
    public ClientResourcesChanged(ClientId clientId) {
        super(clientId);
    }
}