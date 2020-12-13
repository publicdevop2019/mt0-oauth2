package com.mt.identityaccess.domain.model.app;

import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RootBizClientPatchMiddleLayer extends TypedClass<RootBizClientPatchMiddleLayer> {
    private String description;
    private String name;
    private boolean resourceIndicator;
    private Set<ScopeEnum> scopeEnums;
    private Set<GrantTypeEnum> grantTypeEnums;
    private Set<BizClientAuthorityEnum> grantedAuthorities;
    private Integer accessTokenValiditySeconds;
    private Set<String> resourceIds;

    public RootBizClientPatchMiddleLayer(BizClient bizClient) {
        super(RootBizClientPatchMiddleLayer.class);
        this.description = bizClient.getDescription();
        this.name = bizClient.getName();
        this.resourceIndicator = bizClient.isResourceIndicator();
        this.scopeEnums = bizClient.getScopeEnums();
        this.grantTypeEnums = bizClient.getGrantTypeEnums();
        this.accessTokenValiditySeconds = bizClient.getAccessTokenValiditySeconds();
        this.resourceIds = bizClient.getFollowing().stream().map(e -> e.getId().toString()).collect(Collectors.toSet());
        this.grantedAuthorities = bizClient.getGrantedAuthorities();
    }

    public RootBizClientPatchMiddleLayer() {
        super(RootBizClientPatchMiddleLayer.class);
    }
}
