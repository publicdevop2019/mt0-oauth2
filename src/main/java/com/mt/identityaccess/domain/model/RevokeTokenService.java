package com.mt.identityaccess.domain.model;

import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.user.UserId;

public interface RevokeTokenService {
    void revokeClientToken(ClientId clientId);
    void revokeUserToken(UserId userId);
}
