package com.mt.identityaccess.domain.model.client;

import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ClientPatchingMiddleLayer extends TypedClass<ClientPatchingMiddleLayer> {
    private String description;
    private String name;
    private boolean resourceIndicator;
    private Set<Scope> scopeEnums;
    private Set<GrantType> grantTypeEnums;
    private Set<Authority> grantedAuthorities;
    private Integer accessTokenValiditySeconds;
    private Set<String> resourceIds;

    public ClientPatchingMiddleLayer(Client bizClient) {
        super(ClientPatchingMiddleLayer.class);
        this.description = bizClient.basicClientDetail().getDescription();
        this.name = bizClient.basicClientDetail().getName();
        this.resourceIndicator = bizClient.basicClientDetail().isAccessible();
        this.scopeEnums = bizClient.basicClientDetail().getScopes();
        this.grantTypeEnums = bizClient.totalGrantTypes();
        this.accessTokenValiditySeconds = bizClient.accessTokenDetail().getAccessTokenValiditySeconds();
        this.resourceIds = bizClient.basicClientDetail().getResources().stream().map(ClientId::id).collect(Collectors.toSet());
        this.grantedAuthorities = bizClient.basicClientDetail().getAuthorities();
    }

    public ClientPatchingMiddleLayer() {
        super(ClientPatchingMiddleLayer.class);
    }
}
