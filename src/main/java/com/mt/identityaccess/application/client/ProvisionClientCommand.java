package com.mt.identityaccess.application.client;

import com.mt.identityaccess.domain.model.client.Authority;
import com.mt.identityaccess.domain.model.client.GrantType;
import com.mt.identityaccess.domain.model.client.Scope;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Set;

@Data
@Slf4j
public class ProvisionClientCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String clientSecret;
    private boolean hasSecret;
    private String description;
    private String name;

    private Set<GrantType> grantTypeEnums;

    private Set<Authority> grantedAuthorities;

    private Set<Scope> scopeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private boolean resourceIndicator;

    private boolean autoApprove;
}
