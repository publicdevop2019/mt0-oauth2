package com.mt.identityaccess.domain.model.endpoint.event;

import com.mt.common.domain_event.DomainEvent;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.identityaccess.domain.model.endpoint.EndpointId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.Set;
import java.util.stream.Collectors;

public class EndpointsBatchDeleted extends DomainEvent {
    public EndpointsBatchDeleted(Set<EndpointId> endpointIds) {
        super(endpointIds.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
    }
}
