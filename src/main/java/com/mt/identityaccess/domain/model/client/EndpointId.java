package com.mt.identityaccess.domain.model.client;

import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.domain.DomainRegistry;

public class EndpointId extends DomainId {
    public EndpointId() {
        super();
        Long id = DomainRegistry.uniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0E" + s.toUpperCase());
    }

    public EndpointId(String domainId) {
        super(domainId);
    }
}
