package com.hw.aggregate.client.command;

import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import com.hw.aggregate.client.model.GrantTypeEnum;
import com.hw.aggregate.client.model.ScopeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class CreateClientCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String clientSecret;
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
