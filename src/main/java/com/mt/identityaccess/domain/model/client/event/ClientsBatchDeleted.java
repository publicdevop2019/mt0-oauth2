package com.mt.identityaccess.domain.model.client.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientsBatchDeleted extends DomainEvent {
    public ClientsBatchDeleted(Set<ClientId> clientIds) {
        super(clientIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
    }
}
