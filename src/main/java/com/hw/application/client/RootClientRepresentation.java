package com.hw.application.client;

import com.hw.domain.model.client.*;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RootClientRepresentation {
    protected String id;

    protected String name;

    protected String description;

    protected Set<GrantType> grantTypeEnums;

    protected Set<Authority> grantedAuthorities;

    protected Set<Scope> scopeEnums;

    protected Integer accessTokenValiditySeconds;

    protected Set<String> registeredRedirectUri;

    protected Integer refreshTokenValiditySeconds;

    protected Set<String> resourceIds;

    protected boolean resourceIndicator;

    protected boolean autoApprove;

    protected Integer version;
    private String clientSecret;

    private boolean hasSecret;

    public RootClientRepresentation(Client client) {
        id = client.clientId().id();
        name = client.name();
        description = client.description();
        grantTypeEnums = client.totalGrantTypes();
        grantedAuthorities = client.authorities();
        scopeEnums = client.scopes();
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        if (client.authorizationCodeGrant() != null)
            registeredRedirectUri = client.authorizationCodeGrant().redirectUrls();
        if (client.passwordGrant() != null && client.passwordGrant().refreshTokenGrant() != null)
            refreshTokenValiditySeconds = client.passwordGrant().accessTokenValiditySeconds();
        if (!ObjectUtils.isEmpty(client.resources()))
            resourceIds = client.resources().stream().map(ClientId::id).collect(Collectors.toSet());
        resourceIndicator = client.accessible();
        if (client.authorizationCodeGrant() != null)
            autoApprove = client.authorizationCodeGrant().autoApprove();
        version = client.version();
        clientSecret = "masked";
        hasSecret = true;

    }
}
