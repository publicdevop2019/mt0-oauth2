package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientAsResourceDeleted extends ClientEvent{
    public ClientAsResourceDeleted(ClientId clientId) {
        super(clientId);
    }
}