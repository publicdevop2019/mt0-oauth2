package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientSecretChanged extends ClientEvent {
    public ClientSecretChanged(ClientId clientId) {
        super(clientId);
    }
}