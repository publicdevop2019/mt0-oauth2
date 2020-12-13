package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.Authority;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.GrantType;
import com.mt.identityaccess.domain.model.client.Scope;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RootClientCardRepresentation {

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

    public RootClientCardRepresentation(Object client) {
        BeanUtils.copyProperties(client, this);
        resourceIds = ((Client) client).getFollowing().stream().map(e -> e.getId().toString()).collect(Collectors.toSet());
    }
}
