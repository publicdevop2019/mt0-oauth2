package com.hw.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.clazz.eenum.GrantTypeEnum;
import com.hw.clazz.eenum.ScopeEnum;
import com.hw.converter.StringListConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * use different field name to make it more flexible also avoid copy properties type mismatch
 * e.g getting return string instead of enum
 */
@Entity
@Table(name = "client")
@SequenceGenerator(name = "clientId_gen", sequenceName = "clientId_gen", initialValue = 100)
@Data
public class Client extends Auditable implements ClientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "clientId_gen")
    @Setter(AccessLevel.NONE)
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
    @Convert(converter = GrantTypeEnum.GrantTypeConverter.class)
    private Set<GrantTypeEnum> grantTypeEnums;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = ClientAuthorityEnum.ClientAuthorityConverter.class)
    private List<@Valid @NotNull GrantedAuthorityImpl<ClientAuthorityEnum>> grantedAuthority;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = ScopeEnum.ScopeConverter.class)
    private Set<ScopeEnum> scopeEnums;

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

    @Override
    public String getClientId() {
        return clientId;
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

    @Override
    @JsonIgnore
    //TODO remove
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return scopeEnums.stream().map(e -> e.toString().toLowerCase()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return grantTypeEnums.stream().map(e -> e.toString().toLowerCase()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    @JsonIgnore
    //TODO remove
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthority.stream().map(e -> (GrantedAuthority) e).collect(Collectors.toList());
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
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }

}
