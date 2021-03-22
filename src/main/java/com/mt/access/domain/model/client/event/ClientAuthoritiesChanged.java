package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

public class ClientAuthoritiesChanged extends ClientEvent{
    public ClientAuthoritiesChanged(ClientId clientId) {
        super(clientId);
    }
}