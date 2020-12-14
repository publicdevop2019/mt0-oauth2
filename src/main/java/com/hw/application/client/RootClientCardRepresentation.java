package com.hw.application.client;

import com.hw.domain.model.client.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RootClientCardRepresentation {

    protected String id;

    protected String name;

    protected String description;

    protected Set<GrantType> grantTypeEnums;

    protected Set<Authority> grantedAuthorities;

    protected Set<Scope> scopeEnums;

    protected int accessTokenValiditySeconds;

    protected Set<String> registeredRedirectUri;

    protected int refreshTokenValiditySeconds;

    protected Set<String> resourceIds;

    protected boolean resourceIndicator;

    protected boolean autoApprove;

    protected int version;

    public RootClientCardRepresentation(Object client) {
        Client client1 = (Client) client;
        id = client1.clientId().id();
        name = client1.name();
        grantTypeEnums = client1.totalGrantTypes();
        grantedAuthorities = client1.authorities();
        scopeEnums = client1.scopes();
        accessTokenValiditySeconds = client1.accessTokenDetail().getAccessTokenValiditySeconds();
        registeredRedirectUri = client1.authorizationCodeGrantDetail().redirectUrls();
        refreshTokenValiditySeconds = client1.refreshTokenGrantDetail().refreshTokenValiditySeconds();
        resourceIds = client1.resources().stream().map(ClientId::id).collect(Collectors.toSet());
        resourceIndicator = client1.accessible();
    }
}
