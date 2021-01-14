package com.mt.identityaccess.application.revoke_token;

import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.common.persistence.QueryConfig;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import com.mt.identityaccess.domain.model.revoke_token.RevokeTokenId;
import com.mt.identityaccess.domain.model.user.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RevokeTokenApplicationService {
    @SubscribeForEvent
    @Transactional
    public String create(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, changeId, revokeTokenId, () -> {
            boolean b = DomainRegistry.authenticationService().userInRole(Role.ROLE_ROOT);
            if (!b && command.getType().equals(RevokeToken.TokenType.CLIENT))
                throw new IllegalArgumentException("type can only be user");
            DomainRegistry.revokeTokenRepository().add(new RevokeToken(command.getId(), revokeTokenId, command.getType()));
            return revokeTokenId;
        }, RevokeToken.class);
    }

    @SubscribeForEvent
    @Transactional
    public String internalOnlyCreate(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, changeId, revokeTokenId, () -> {
            DomainRegistry.revokeTokenRepository().add(new RevokeToken(command.getId(), revokeTokenId, command.getType()));
            return revokeTokenId;
        }, RevokeToken.class);
    }

    public SumPagedRep<RevokeToken> revokeTokens(String queryParam, String pageParam, String config) {
        return DomainRegistry.revokeTokenRepository().revokeTokensOfQuery(new RevokeTokenQuery(queryParam), new DefaultPaging(pageParam), new QueryConfig(config));
    }
}
