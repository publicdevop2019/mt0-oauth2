package com.mt.identityaccess.infrastructure;

import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.revoke_token.CreateRevokeTokenCommand;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import com.mt.identityaccess.domain.service.RevokeTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class InternalRevokeTokenService implements RevokeTokenService {

    @Override
    public void revokeClientToken(DomainId domainId) {
        revoke(domainId.getDomainId(), RevokeToken.TokenType.CLIENT);
    }

    @Override
    public void revokeUserToken(DomainId domainId) {
        revoke(domainId.getDomainId(), RevokeToken.TokenType.USER);
    }

    private void revoke(String id, RevokeToken.TokenType targetType) {
        CreateRevokeTokenCommand createRevokeTokenCommand = new CreateRevokeTokenCommand(id, targetType);
        ApplicationServiceRegistry.revokeTokenApplicationService().internalOnlyCreate(createRevokeTokenCommand, UUID.randomUUID().toString());
        log.debug("complete revoke token");
    }
}
