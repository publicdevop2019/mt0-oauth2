package com.mt.identityaccess.domain.model.client.event;

import com.mt.identityaccess.config.DomainEvent;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Date;
@Entity
@NoArgsConstructor
public class ClientReplaced extends DomainEvent{
    public ClientReplaced(ClientId clientId) {
        super(clientId);
    }
}