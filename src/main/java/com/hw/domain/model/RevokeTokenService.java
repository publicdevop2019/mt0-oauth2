package com.hw.domain.model;

import com.hw.domain.model.client.ClientId;
import com.hw.domain.model.user.UserId;

public interface RevokeTokenService {
    void revokeClientToken(ClientId clientId);
    void revokeUserToken(UserId userId);
}
