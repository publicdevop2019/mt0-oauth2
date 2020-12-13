package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class AppBizClientRep implements ClientDetails {
    private Long id;
    private String clientSecret;
    private Set<GrantTypeEnum> grantTypeEnums;
    private Set<BizClientAuthorityEnum> grantedAuthorities;
    private Set<ScopeEnum> scopeEnums;
    private Integer accessTokenValiditySeconds;
    private Set<String> registeredRedirectUri;
    private Integer refreshTokenValiditySeconds;
    private Set<String> resourceIds;
    private Boolean autoApprove;
    private Boolean hasSecret;

    public AppBizClientRep(BizClient bizClient) {
        BeanUtils.copyProperties(bizClient, this);
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
        return hasSecret;
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
        return grantedAuthorities.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toList());
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
