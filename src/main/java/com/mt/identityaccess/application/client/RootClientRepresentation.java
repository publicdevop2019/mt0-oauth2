package com.mt.identityaccess.application.client;

import com.mt.identityaccess.domain.model.client.*;
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
        id = client.getClientId().getClientId();
        name = client.getName();
        description = client.getDescription();
        grantTypeEnums = client.totalGrantTypes();
        grantedAuthorities = client.getAuthorities();
        scopeEnums = client.getScopes();
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        if (client.getAuthorizationCodeGrant() != null)
            registeredRedirectUri = client.getAuthorizationCodeGrant().getRedirectUrls();
        if (client.getPasswordGrant() != null && client.getPasswordGrant().getRefreshTokenGrant() != null)
            refreshTokenValiditySeconds = client.getPasswordGrant().getAccessTokenValiditySeconds();
        if (!ObjectUtils.isEmpty(client.getResources()))
            resourceIds = client.getResources().stream().map(ClientId::getClientId).collect(Collectors.toSet());
        resourceIndicator = client.isAccessible();
        if (client.getAuthorizationCodeGrant() != null)
            autoApprove = client.getAuthorizationCodeGrant().isAutoApprove();
        version = client.getVersion();
        clientSecret = "masked";
        hasSecret = true;

    }
}
