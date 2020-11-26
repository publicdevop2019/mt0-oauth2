package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import lombok.Data;

@Data
public class RootBizClientRep extends RootBizClientCardRep {

    private String clientSecret;

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
        this.version = client.getVersion();
    }
}
