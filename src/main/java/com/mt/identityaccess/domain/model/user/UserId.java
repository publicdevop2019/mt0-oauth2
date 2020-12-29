package com.mt.identityaccess.domain.model.user;

import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.domain.DomainRegistry;

public class UserId extends DomainId {
    public UserId() {
        super();
        Long id = DomainRegistry.uniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0U" + s.toUpperCase());
    }

    public UserId(String domainId) {
        super(domainId);
    }
}
