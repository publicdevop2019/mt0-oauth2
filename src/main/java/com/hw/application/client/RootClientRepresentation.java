package com.hw.application.client;

import com.hw.domain.model.client.Authority;
import com.hw.domain.model.client.Client;
import com.hw.domain.model.client.GrantType;
import com.hw.domain.model.client.Scope;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class RootClientRepresentation{
    protected Long id;

    protected String name;

    protected String description;

    protected Set<GrantType> grantTypeEnums;

    protected Set<Authority> grantedAuthorities;

    protected Set<Scope> scopeEnums;

    protected Integer accessTokenValiditySeconds;

    protected Set<String> registeredRedirectUri;

    protected Integer refreshTokenValiditySeconds;

    protected Set<String> resourceIds;

    protected Boolean resourceIndicator;

    protected Boolean autoApprove;

    protected Integer version;
    private String clientSecret;

    private Boolean hasSecret;

    public RootClientRepresentation(Client client) {
        BeanUtils.copyProperties(client, this);
    }
}
