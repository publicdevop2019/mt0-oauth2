package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.domainId.DomainId;

public interface RevokeTokenService {
    void revokeToken(DomainId domainId);
}
