package com.mt.identityaccess.application.client;

import com.mt.identityaccess.domain.model.client.*;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
@Getter
public class ClientPatchingCommand {
    private String description;
    private String name;
    private boolean resourceIndicator;
    private Set<Scope> scopeEnums;
    private Set<GrantType> grantTypeEnums;
    private Set<Authority> grantedAuthorities;
    private Integer accessTokenValiditySeconds;
    private Set<String> resourceIds;

    public ClientPatchingCommand(Client bizClient) {
        this.description = bizClient.basicClientDetail().description();
        this.name = bizClient.basicClientDetail().name();
        this.resourceIndicator = bizClient.basicClientDetail().accessible();
        this.scopeEnums = bizClient.basicClientDetail().scopes();
        this.grantTypeEnums = bizClient.totalGrantTypes();
        this.accessTokenValiditySeconds = bizClient.accessTokenDetail().getAccessTokenValiditySeconds();
        this.resourceIds = bizClient.basicClientDetail().resources().stream().map(ClientId::id).collect(Collectors.toSet());
        this.grantedAuthorities = bizClient.basicClientDetail().authorities();
    }

}
