package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RootClientCardRepresentation {

    protected Long id;

    protected String name;

    protected String description;

    protected Set<GrantType> grantTypeEnums;

    protected Set<Authority> grantedAuthorities;

    protected Set<Scope> scopeEnums;

    protected Integer accessTokenValiditySeconds;

    protected Set<String> registeredRedirectUri;

    protected Integer refreshTokenValiditySeconds;

    protected Set<String> resourceIds;

    protected Boolean resourceIndicator;

    protected Boolean autoApprove;

    protected Integer version;

    public RootClientCardRepresentation(Object client) {
        Client client1 = (Client) client;
        id = client1.basicClientDetail().getClientId().persistentId();
        name = client1.basicClientDetail().name();
        grantTypeEnums=client1.totalGrantTypes();
        grantedAuthorities=client1.basicClientDetail().authorities();
        scopeEnums=client1.basicClientDetail().scopes();
        accessTokenValiditySeconds=client1.accessTokenDetail().getAccessTokenValiditySeconds();
        registeredRedirectUri=client1.authorizationCodeGrantDetail().redirectUrls();
        refreshTokenValiditySeconds=client1.refreshTokenGrantDetail().refreshTokenValiditySeconds();
        resourceIds = client1.basicClientDetail().resources().stream().map(ClientId::id).collect(Collectors.toSet());
        resourceIndicator=client1.basicClientDetail().isAccessible();
    }
}
