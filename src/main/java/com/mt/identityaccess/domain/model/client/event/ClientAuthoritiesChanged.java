package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

public class ClientAuthoritiesChanged extends ClientEvent{
    public ClientAuthoritiesChanged(ClientId clientId) {
        super(clientId);
    }
}