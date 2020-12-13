package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.app.BizClient;
import com.mt.identityaccess.domain.model.app.BizClientAuthorityEnum;
import com.mt.identityaccess.domain.model.app.GrantTypeEnum;
import com.mt.identityaccess.domain.model.app.ScopeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.stream.Collectors;

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
        resourceIds = client.getFollowing().stream().map(e -> e.getId().toString()).collect(Collectors.toSet());
    }
}
