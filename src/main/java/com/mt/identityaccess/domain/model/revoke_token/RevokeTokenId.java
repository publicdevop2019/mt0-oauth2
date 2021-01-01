package com.mt.identityaccess.domain.model.revoke_token;

import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.domain.DomainRegistry;

public class RevokeTokenId extends DomainId {
    public RevokeTokenId() {
        super();
        Long id = DomainRegistry.uniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0T" + s.toUpperCase());
    }

    public RevokeTokenId(String domainId) {
        super(domainId);
    }
}
