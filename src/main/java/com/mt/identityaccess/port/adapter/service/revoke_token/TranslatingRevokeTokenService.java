package com.mt.identityaccess.port.adapter.service.revoke_token;

import com.mt.identityaccess.domain.model.RevokeTokenService;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.user.UserId;
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
