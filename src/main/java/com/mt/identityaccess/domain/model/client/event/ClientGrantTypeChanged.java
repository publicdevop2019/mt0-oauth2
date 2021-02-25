package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientGrantTypeChanged extends ClientEvent{
    public ClientGrantTypeChanged(ClientId clientId) {
        super(clientId);
    }
}