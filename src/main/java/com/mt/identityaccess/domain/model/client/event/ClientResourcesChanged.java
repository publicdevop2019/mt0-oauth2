package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientResourcesChanged extends ClientEvent{
    public ClientResourcesChanged(ClientId clientId) {
        super(clientId);
    }
}