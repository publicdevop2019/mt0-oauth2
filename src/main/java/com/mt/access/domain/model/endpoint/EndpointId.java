package com.mt.access.domain.model.endpoint;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.access.domain.DomainRegistry;

public class EndpointId extends DomainId {
    public EndpointId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0E" + s.toUpperCase());
    }

    public EndpointId(String domainId) {
        super(domainId);
    }
}
