package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.user.UserId;

public interface RevokeTokenService {
    void revokeClientToken(DomainId domainId);
    void revokeUserToken(DomainId domainId);
}
