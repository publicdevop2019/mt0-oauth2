package com.mt.identityaccess.domain.model.client.event;

import com.mt.common.domain.model.DomainEvent;
import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.EndpointId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
public class EndpointsBatchDeleted extends DomainEvent {
    public EndpointsBatchDeleted(Set<EndpointId> endpointIds) {
        super(endpointIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
    }
}
