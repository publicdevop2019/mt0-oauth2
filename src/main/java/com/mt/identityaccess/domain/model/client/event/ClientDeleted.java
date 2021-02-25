package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientDeleted extends ClientEvent{
    public ClientDeleted(ClientId clientId) {
        super(clientId);
    }
}