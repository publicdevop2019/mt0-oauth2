package com.mt.identityaccess.application.command;

import com.mt.identityaccess.domain.model.client.BizClientAuthorityEnum;
import com.mt.identityaccess.domain.model.client.GrantTypeEnum;
import com.mt.identityaccess.domain.model.client.ScopeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Set;

@Data
@Slf4j
public class RootCreateBizClientCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String clientSecret;
    private boolean hasSecret;
    private String description;
    private String name;

    private Set<GrantTypeEnum> grantTypeEnums;

    private Set<BizClientAuthorityEnum> grantedAuthorities;

    private Set<ScopeEnum> scopeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;
}
