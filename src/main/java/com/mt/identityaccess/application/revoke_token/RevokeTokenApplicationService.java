package com.mt.identityaccess.application.revoke_token;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.client.ClientQuery;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.event.*;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import com.mt.identityaccess.domain.model.revoke_token.RevokeTokenId;
import com.mt.identityaccess.domain.model.user.Role;
import com.mt.identityaccess.domain.model.user.event.UserAuthorityChanged;
import com.mt.identityaccess.domain.model.user.event.UserDeleted;
import com.mt.identityaccess.domain.model.user.event.UserGetLocked;
import com.mt.identityaccess.domain.model.user.event.UserPasswordChanged;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class RevokeTokenApplicationService {
    @SubscribeForEvent
    @Transactional
    public String create(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId(command.getId());
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, changeId, revokeTokenId, () -> {
            boolean b = DomainRegistry.authenticationService().userInRole(Role.ROLE_ROOT);
            if (!b && revokeTokenId.getType().equals(RevokeToken.TokenType.CLIENT))
                throw new IllegalArgumentException("type can only be user");
            DomainRegistry.revokeTokenRepository().add(new RevokeToken(revokeTokenId));
            return revokeTokenId;
        }, RevokeToken.class);
    }

    @SubscribeForEvent
    @Transactional
    public String internalOnlyCreate(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId(command.getId());
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, changeId, revokeTokenId, () -> {
            DomainRegistry.revokeTokenRepository().add(new RevokeToken(revokeTokenId));
            return revokeTokenId;
        }, RevokeToken.class);
    }

    public SumPagedRep<RevokeToken> revokeTokens(String queryParam, String pageParam, String config) {
        return DomainRegistry.revokeTokenRepository().revokeTokensOfQuery(new RevokeTokenQuery(queryParam), new PageConfig(pageParam,2000), new QueryConfig(config));
    }

    @Transactional
    public void handleChange(StoredEvent event) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null,null, event.getId().toString(), (ignored) -> {
            if (USER_EVENTS.contains(event.getName())) {
                DomainEvent deserialize = DomainRegistry.customObjectSerializer().deserialize(event.getEventBody(), DomainEvent.class);
                DomainRegistry.revokeTokenService().revokeToken(deserialize.getDomainId());
            } else if (CLIENT_EVENTS.contains(event.getName())) {
                DomainEvent deserialize = DomainRegistry.customObjectSerializer().deserialize(event.getEventBody(), DomainEvent.class);
                DomainRegistry.revokeTokenService().revokeToken(deserialize.getDomainId());
                //revoke who is accessing this client's token
                if (ClientAccessibilityRemoved.class.getName().equals(event.getName())) {
                    Set<Client> clientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(ClientQuery.resourceIds(deserialize.getDomainId().getDomainId()));
                    clientsOfQuery.forEach(e -> {
                        DomainRegistry.revokeTokenService().revokeToken(e.getClientId());
                    });
                }
            } else if (ClientResourceCleanUpCompleted.class.getName().equals(event.getName())) {
                DomainEvent deserialize = DomainRegistry.customObjectSerializer().deserialize(event.getEventBody(), DomainEvent.class);
                //revoke deleted client token
                deserialize.getDomainIds().forEach(e -> DomainRegistry.revokeTokenService().revokeToken(e));
            }
        }, RevokeToken.class);
    }

    private static final Set<String> USER_EVENTS = new HashSet<>();

    static {
        USER_EVENTS.add(UserAuthorityChanged.class.getName());
        USER_EVENTS.add(UserDeleted.class.getName());
        USER_EVENTS.add(UserGetLocked.class.getName());
        USER_EVENTS.add(UserPasswordChanged.class.getName());
    }

    private static final Set<String> CLIENT_EVENTS = new HashSet<>();

    static {
        CLIENT_EVENTS.add(ClientAccessibilityRemoved.class.getName());
        CLIENT_EVENTS.add(ClientAccessTokenValiditySecondsChanged.class.getName());
        CLIENT_EVENTS.add(ClientAuthoritiesChanged.class.getName());
        CLIENT_EVENTS.add(ClientGrantTypeChanged.class.getName());
        CLIENT_EVENTS.add(ClientRefreshTokenChanged.class.getName());
        CLIENT_EVENTS.add(ClientDeleted.class.getName());
        CLIENT_EVENTS.add(ClientResourcesChanged.class.getName());
        CLIENT_EVENTS.add(ClientScopesChanged.class.getName());
        CLIENT_EVENTS.add(ClientSecretChanged.class.getName());
    }
}
