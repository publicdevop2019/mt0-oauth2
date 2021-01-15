package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientAsResourceDeleted extends ClientEvent{
    public ClientAsResourceDeleted(ClientId clientId) {
        super(clientId);
    }
}