package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientTokenDetailChanged extends ClientEvent{
    public ClientTokenDetailChanged(ClientId clientId) {
        super(clientId);
    }
}