package com.hw.application.client;

import com.hw.domain.model.client.*;
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
    private int accessTokenValiditySeconds = 0;
    private Set<String> resourceIds;

    public ClientPatchingCommand(Client bizClient) {
        this.description = bizClient.description();
        this.name = bizClient.name();
        this.resourceIndicator = bizClient.accessible();
        this.scopeEnums = bizClient.scopes();
        this.grantTypeEnums = bizClient.totalGrantTypes();
        if (bizClient.clientCredentialsGrantDetail() != null) {
            this.accessTokenValiditySeconds = bizClient.clientCredentialsGrantDetail().accessTokenValiditySeconds();
        } else if (bizClient.passwordGrantDetail() != null) {
            this.accessTokenValiditySeconds = bizClient.passwordGrantDetail().accessTokenValiditySeconds();
        } else if (bizClient.refreshTokenGrantDetail() != null) {
            this.accessTokenValiditySeconds = bizClient.refreshTokenGrantDetail().accessTokenValiditySeconds();
        } else if (bizClient.authorizationCodeGrantDetail() != null) {
            this.accessTokenValiditySeconds = bizClient.authorizationCodeGrantDetail().accessTokenValiditySeconds();
        }
        this.resourceIds = bizClient.resources().stream().map(ClientId::id).collect(Collectors.toSet());
        this.grantedAuthorities = bizClient.authorities();
    }

}
