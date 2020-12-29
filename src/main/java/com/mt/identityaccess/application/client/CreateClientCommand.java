package com.mt.identityaccess.application.client;

import com.mt.identityaccess.domain.model.client.Authority;
import com.mt.identityaccess.domain.model.client.GrantType;
import com.mt.identityaccess.domain.model.client.Scope;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Set;

@Data
@Slf4j
public class CreateClientCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String clientSecret;
    private boolean hasSecret = false;
    private String description;
    private String name;

    private Set<GrantType> grantTypeEnums;

    private Set<Authority> grantedAuthorities;

    private Set<Scope> scopeEnums;

    private int accessTokenValiditySeconds = 0;
    @Nullable
    private Set<String> registeredRedirectUri;
    private int refreshTokenValiditySeconds = 0;
    @Nullable
    private Set<String> resourceIds;

    private boolean resourceIndicator = false;

    private boolean autoApprove = false;
}
