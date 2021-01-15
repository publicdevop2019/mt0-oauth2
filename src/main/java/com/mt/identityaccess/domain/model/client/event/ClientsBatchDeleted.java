package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.domain.model.client.ClientId;

import java.util.Set;

public class ClientsBatchDeleted extends ClientEvent {
    public ClientsBatchDeleted(Set<ClientId> clientIds) {
        super(clientIds);
    }
}
