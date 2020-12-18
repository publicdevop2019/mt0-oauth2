package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.config.DomainEvent;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class ClientSecretChanged extends DomainEvent {
    public ClientSecretChanged(ClientId clientId) {
        super(clientId);
    }
}