package com.hw.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.clazz.eenum.GrantTypeEnum;
import com.hw.clazz.eenum.ScopeEnum;
import com.hw.converter.StringListConverter;
import com.hw.shared.Auditable;
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
@SequenceGenerator(name = "entityId_gen", sequenceName = "entityId_seq", initialValue = 100)
@Data
public class Client extends Auditable implements ClientDetails {

    @Id
    @GeneratedValue(generator = "entityId_gen")
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
    private List<@Valid @NotNull GrantedAuthorityImpl<ClientAuthorityEnum>> grantedAuthorities;

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

    @Column
    @NotNull
    @NotEmpty
    @Convert(converter = StringListConverter.class)
    private Set<String> resourceIds;

    /**
     * indicates if a client is a resource, if true resource id will default to client id
     */
    @Column
    @NotNull
    private Boolean resourceIndicator;

    /**
     * indicates if a authorization_code client can be auto approved
     */
    @Column
    @Nullable
    private Boolean autoApprove;

    /**
     * this field is not used in spring oauth2,
     * client with no secret requires empty secret (mostly encoded)
     * below is empty string "" encoded, use if needed
     * $2a$10$KRp4.vK8F8MYLJGEz7im8.71T2.vFQj/rrNLQLOLPEADuv0Gdg.x6
     */
    @Column
    @NotNull
    private Boolean hasSecret;

    /**
     * JsonIgnore make sure filed does not get print two times
     */
    @Override
    @JsonIgnore
    public boolean isSecretRequired() {
        return hasSecret;
    }


    @Override
    @JsonIgnore
    public boolean isScoped() {
        return true;
    }

    @Override
    @JsonIgnore
    public Set<String> getScope() {
        return scopeEnums.stream().map(e -> e.toString().toLowerCase()).collect(Collectors.toSet());
    }

    @Override
    @JsonIgnore
    public Set<String> getAuthorizedGrantTypes() {
        return grantTypeEnums.stream().map(e -> e.toString().toLowerCase()).collect(Collectors.toSet());
    }

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthorities.stream().map(e -> (GrantedAuthority) e).collect(Collectors.toList());
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return false;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }

}
