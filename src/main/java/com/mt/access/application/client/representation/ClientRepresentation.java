package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.*;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ClientRepresentation {
    protected String id;

    protected String name;

    protected String description;

    protected Set<GrantType> grantTypeEnums;

    protected Set<Role> grantedAuthorities;

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

    public ClientRepresentation(Client client) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        description = client.getDescription();
        grantTypeEnums = client.totalGrantTypes();
        grantedAuthorities = client.getRoles();
        scopeEnums = client.getScopes();
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        if (client.getAuthorizationCodeGrant() != null)
            registeredRedirectUri = client.getAuthorizationCodeGrant().getRedirectUrls().stream().map(RedirectURL::getValue).collect(Collectors.toSet());
        if (client.getPasswordGrant() != null && client.getPasswordGrant().getRefreshTokenGrant() != null)
            refreshTokenValiditySeconds = client.getPasswordGrant().getAccessTokenValiditySeconds();
        if (!ObjectUtils.isEmpty(client.getResources()))
            resourceIds = client.getResources().stream().map(ClientId::getDomainId).collect(Collectors.toSet());
        resourceIndicator = client.isAccessible();
        if (client.getAuthorizationCodeGrant() != null)
            autoApprove = client.getAuthorizationCodeGrant().isAutoApprove();
        version = client.getVersion();
        clientSecret = "masked";
        hasSecret = true;

    }
}
