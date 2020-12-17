package com.hw.port.adapter.service.revoke_token;

import com.hw.domain.model.RevokeTokenService;
import com.hw.domain.model.client.ClientId;
import com.hw.domain.model.user.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TranslatingRevokeTokenService implements RevokeTokenService {
    @Autowired
    RevokeTokenAdapter revokeTokenAdapter;

    @Override
    public void revokeClientToken(ClientId clientId) {
        revokeTokenAdapter.revoke(clientId.id(), "CLIENT");
    }

    @Override
    public void revokeUserToken(UserId userId) {
        revokeTokenAdapter.revoke(userId.id(), "USER");
    }
}