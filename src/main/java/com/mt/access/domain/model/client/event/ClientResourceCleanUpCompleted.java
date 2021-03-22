package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;

import java.util.Set;

public class ClientResourceCleanUpCompleted extends ClientEvent {
    public ClientResourceCleanUpCompleted(Set<ClientId> pendingRevoked) {
        super(pendingRevoked);
    }
}
