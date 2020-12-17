package com.hw.application.client;

import com.hw.application.representation.AppBizUserRep;
import com.hw.domain.model.client.Authority;
import com.hw.domain.model.client.Client;
import com.hw.domain.model.client.GrantType;
import com.hw.domain.model.client.Scope;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
public class ClientDetailsRepresentation implements ClientDetails {
    private Long id;
    private String clientSecret;
    private Set<GrantType> grantTypeEnums;
    private Set<Authority> grantedAuthorities;
    private Set<Scope> scopeEnums;
    private int accessTokenValiditySeconds;
    private Set<String> registeredRedirectUri;
    private int refreshTokenValiditySeconds;
    private Set<String> resourceIds;
    private boolean autoApprove = false;

    public ClientDetailsRepresentation(Client client) {
        setId(client.id());
        setClientSecret(client.secret());
        setGrantTypeEnums(client.totalGrantTypes());
        setGrantedAuthorities(client.authorities());
        setScopeEnums(client.scopes());
        setAccessTokenValiditySeconds(client.accessTokenValiditySeconds());
    }

    @Override
    public String getClientId() {
        return id.toString();
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return scopeEnums.stream().map(Enum::name).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return grantTypeEnums.stream().map(e -> e.name().toLowerCase()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthorities.stream().map(AppBizUserRep.GrantedAuthorityImpl::new).collect(Collectors.toList());
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return autoApprove;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }


}
