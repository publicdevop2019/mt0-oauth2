package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientUpdated extends ClientEvent{
    public ClientUpdated(ClientId clientId) {
        super(clientId);
    }
}