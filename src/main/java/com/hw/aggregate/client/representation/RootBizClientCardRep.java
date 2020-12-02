package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import com.hw.aggregate.client.model.GrantTypeEnum;
import com.hw.aggregate.client.model.ScopeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

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

    protected Integer version;

    public RootBizClientCardRep(BizClient client) {
        BeanUtils.copyProperties(client, this);
    }
}
