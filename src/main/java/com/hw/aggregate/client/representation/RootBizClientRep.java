package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RootBizClientRep {
    private Long id;

    private String name;

    private String clientSecret;

    private String description;

    private Set<GrantTypeEnum> grantTypeEnums;

    private Set<BizClientAuthorityEnum> grantedAuthorities;

    private Set<ScopeEnum> scopeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;

    private Boolean hasSecret;

    public RootBizClientRep(BizClient client) {
        this.id = client.getId();
        this.name = client.getName();
        this.description = client.getDescription();
        this.clientSecret = null;
        this.grantTypeEnums = client.getGrantTypeEnums();
        this.grantedAuthorities = client.getGrantedAuthorities();
        this.scopeEnums = client.getScopeEnums();
        this.accessTokenValiditySeconds = client.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = client.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = client.getRefreshTokenValiditySeconds();
        this.resourceIds = client.getResourceIds();
        this.resourceIndicator = client.getResourceIndicator();
        this.autoApprove = client.getAutoApprove();
        this.hasSecret = client.getHasSecret();
    }
}
