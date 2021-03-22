package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientScopesChanged extends ClientEvent{
    public ClientScopesChanged(ClientId clientId) {
        super(clientId);
    }
}