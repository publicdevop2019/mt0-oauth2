package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.domainId.DomainId;

public interface RevokeTokenService {
    void revokeClientToken(DomainId domainId);
    void revokeUserToken(DomainId domainId);
}
