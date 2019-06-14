package com.hw.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.converter.GrantedAuthorityConverter;
import com.hw.converter.StringListConverter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "oauth_client")
@SequenceGenerator(name = "clientId_gen", sequenceName = "clientId_gen", initialValue = 100)
public class OAuthClient extends Auditable implements ClientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "clientId_gen")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String clientId;

    @Nullable
    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String clientSecret;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private Set<@NotBlank String> authorizedGrantTypes;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = GrantedAuthorityConverter.class)
    private Collection<@Valid @NotNull GrantedAuthorityImpl> grantedAuthority;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private Set<@NotBlank String> scope;

    @Min(value = 0)
    @Column(nullable = false)
    private Integer accessTokenValiditySeconds;

    @Column
    @Nullable
    @Convert(converter = StringListConverter.class)
    private Set<String> registeredRedirectUri;

    @Column
    @Nullable
    private Integer refreshTokenValiditySeconds;

    /**
     * this field is not used in spring oauth2,
     * client with no secret requires empty secret (mostly encoded)
     * below is empty string "" encoded, use if needed
     * $2a$10$KRp4.vK8F8MYLJGEz7im8.71T2.vFQj/rrNLQLOLPEADuv0Gdg.x6
     */
    @Column
    @NotNull
    private Boolean hasSecret;

    public Boolean getHasSecret() {
        return hasSecret;
    }

    public void setHasSecret(Boolean hasSecret) {
        this.hasSecret = hasSecret;
    }

    public Collection<GrantedAuthorityImpl> getGrantedAuthority() {
        return grantedAuthority;
    }

    public void setGrantedAuthority(Collection<GrantedAuthorityImpl> grantedAuthoritys) {
        this.grantedAuthority = grantedAuthoritys;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return null;
    }

    @Override
    public boolean isSecretRequired() {
        return hasSecret;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    public OAuthClient setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    @JsonIgnore
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return new HashSet<>(scope);
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return new HashSet<>(authorizedGrantTypes);
    }

    public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthority.stream().map(e -> (GrantedAuthority) e).collect(Collectors.toList());
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(@Nullable Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }

}
