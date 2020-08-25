package com.hw.aggregate.client.command;

import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import com.hw.aggregate.client.model.GrantTypeEnum;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.client.model.ScopeEnum;
import lombok.Data;

import java.util.List;
import java.util.Set;
@Data
public class CreateClientCommand {
    private String clientId;
    private String clientSecret;
    private String description;

    private Set<GrantTypeEnum> grantTypeEnums;

    private List<GrantedAuthorityImpl<BizClientAuthorityEnum>> grantedAuthorities;

    private Set<ScopeEnum> scopeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;
}
