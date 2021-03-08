package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointReloadRequested;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {
    public void reloadEndpointCache() {
        DomainEventPublisher.instance().publish(new EndpointReloadRequested());
    }
}
