package com.mt.identityaccess.port.adapter.service.revoke_token;

import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.domain.service.RevokeTokenService;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.user.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TranslatingRevokeTokenService implements RevokeTokenService {
    @Autowired
    RevokeTokenAdapter revokeTokenAdapter;

    @Override
    public void revokeClientToken(DomainId domainId) {
        revokeTokenAdapter.revoke(domainId.getDomainId(), "CLIENT");
    }

    @Override
    public void revokeUserToken(DomainId domainId) {
        revokeTokenAdapter.revoke(domainId.getDomainId(), "USER");
    }
}
