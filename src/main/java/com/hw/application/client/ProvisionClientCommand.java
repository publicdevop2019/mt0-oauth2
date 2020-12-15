package com.hw.application.client;

import com.hw.domain.model.client.Authority;
import com.hw.domain.model.client.GrantType;
import com.hw.domain.model.client.Scope;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
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
    @Nullable
    private Set<String> registeredRedirectUri;
    @Nullable
    private Integer refreshTokenValiditySeconds;
    @Nullable
    private Set<String> resourceIds;

    private boolean resourceIndicator;

    private boolean autoApprove;
}
