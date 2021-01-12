package com.mt.identityaccess.domain.model.client.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

public class ClientScopesChanged extends ClientEvent{
    public ClientScopesChanged(ClientId clientId) {
        super(clientId);
    }
}