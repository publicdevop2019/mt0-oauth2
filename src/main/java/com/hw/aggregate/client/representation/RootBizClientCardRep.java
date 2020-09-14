package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import com.hw.aggregate.client.model.GrantTypeEnum;
import com.hw.aggregate.client.model.ScopeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class RootBizClientCardRep {

    protected Long id;

    protected String name;

    protected String description;

    protected Set<GrantTypeEnum> grantTypeEnums;

    protected Set<BizClientAuthorityEnum> grantedAuthorities;

    protected Set<ScopeEnum> scopeEnums;

    protected Integer accessTokenValiditySeconds;

    protected Set<String> registeredRedirectUri;

    protected Integer refreshTokenValiditySeconds;

    protected Set<String> resourceIds;

    protected Boolean resourceIndicator;

    protected Boolean autoApprove;

    public RootBizClientCardRep(BizClient client) {
        this.id = client.getId();
        this.name = client.getName();
        this.description = client.getDescription();
        this.grantTypeEnums = client.getGrantTypeEnums();
        this.grantedAuthorities = client.getGrantedAuthorities();
        this.scopeEnums = client.getScopeEnums();
        this.accessTokenValiditySeconds = client.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = client.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = client.getRefreshTokenValiditySeconds();
        this.resourceIds = client.getResourceIds();
        this.resourceIndicator = client.getResourceIndicator();
        this.autoApprove = client.getAutoApprove();
    }
}
