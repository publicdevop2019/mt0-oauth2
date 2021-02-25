package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientScopesChanged extends ClientEvent{
    public ClientScopesChanged(ClientId clientId) {
        super(clientId);
    }
}