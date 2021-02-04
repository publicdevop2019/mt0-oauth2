package com.mt.identityaccess.application.client.command;

import com.mt.identityaccess.domain.model.client.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;
@Getter
@NoArgsConstructor
public class ClientPatchCommand {
    private String description;
    private String name;
    private boolean resourceIndicator;
    private Set<Scope> scopeEnums;
    private Set<GrantType> grantTypeEnums;
    private Set<Role> grantedAuthorities;
    private int accessTokenValiditySeconds = 0;
    private Set<String> resourceIds;

    public ClientPatchCommand(Client bizClient) {
        this.description = bizClient.getDescription();
        this.name = bizClient.getName();
        this.resourceIndicator = bizClient.isAccessible();
        this.scopeEnums = bizClient.getScopes();
        this.grantTypeEnums = bizClient.totalGrantTypes();
        this.accessTokenValiditySeconds = bizClient.accessTokenValiditySeconds();
        this.resourceIds = bizClient.getResources().stream().map(ClientId::getDomainId).collect(Collectors.toSet());
        this.grantedAuthorities = bizClient.getRoles();
    }

}
