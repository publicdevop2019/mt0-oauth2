package com.hw.aggregate.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.aggregate.client.AppBizClientApplicationService;
import com.hw.aggregate.client.RevokeBizClientTokenService;
import com.hw.aggregate.client.command.CreateClientCommand;
import com.hw.aggregate.client.command.UpdateClientCommand;
import com.hw.aggregate.client.exception.ClientAlreadyExistException;
import com.hw.aggregate.client.exception.RootClientDeleteException;
import com.hw.aggregate.client.representation.AppBizClientCardRep;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.IdBasedEntity;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * use different field name to make it more flexible also avoid copy properties type mismatch
 * e.g getting return string instead of enum
 */
@Entity
@Table
@Data
public class BizClient extends Auditable implements ClientDetails, IdBasedEntity {
    public static final String ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String ENTITY_RESOURCE_INDICATOR = "resourceIndicator";
    public static final String ENTITY_NAME = "name";
    public static final String ENTITY_GRANT_TYPE_ENUMS = "grantTypeEnums";
    public static final String ENTITY_GRANT_AUTHORITIES = "grantedAuthorities";
    public static final String ENTITY_SCOPE_ENUMS = "scopeEnums";
    public static final String ENTITY_RESOURCE_IDS = "resourceIds";
    @Id
    private Long id;
    private String description;
    @Column(nullable = false)
    private String name;
    @Nullable
    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String clientSecret;
    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = GrantTypeEnum.GrantTypeSetConverter.class)
    private Set<GrantTypeEnum> grantTypeEnums;
    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = BizClientAuthorityEnum.ClientAuthorityConverter.class)
    private Set<BizClientAuthorityEnum> grantedAuthorities;
    @NotNull
    @NotEmpty
    @Column(nullable = false)
    @Convert(converter = ScopeEnum.ScopeSetConverter.class)
    private Set<ScopeEnum> scopeEnums;
    @Min(value = 0)
    @Column(nullable = false)
    private Integer accessTokenValiditySeconds;
    @Column
    @Nullable
    @Convert(converter = StringSetConverter.class)
    private Set<String> registeredRedirectUri;

    @Column
    @Nullable
    private Integer refreshTokenValiditySeconds;

    @Column
    @NotNull
    @Convert(converter = StringSetConverter.class)
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

    public BizClient() {
    }

    private BizClient(long id, CreateClientCommand command) {
        this.id = id;
        this.clientSecret = command.getClientSecret();
        this.description = command.getDescription();
        this.grantTypeEnums = command.getGrantTypeEnums();
        this.grantedAuthorities = command.getGrantedAuthorities();
        this.scopeEnums = command.getScopeEnums();
        this.accessTokenValiditySeconds = command.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = command.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = command.getRefreshTokenValiditySeconds();
        this.resourceIds = command.getResourceIds();
        this.resourceIndicator = command.getResourceIndicator();
        this.autoApprove = command.getAutoApprove();
        this.hasSecret = command.getClientSecret() != null;
        this.name = command.getName();
    }

    public static BizClient create(long id, CreateClientCommand command, AppBizClientApplicationService appBizClientApplicationService, BCryptPasswordEncoder encoder) {
        BizClient client = new BizClient(id, command);
        validateResourceId(client, appBizClientApplicationService);
        validateResourceIndicator(client);
        SumPagedRep<AppBizClientCardRep> appBizClientCardRepSumPagedRep = appBizClientApplicationService.readByQuery("id:" + client.getClientId(), null, null);
        if (appBizClientCardRepSumPagedRep.getData().size() == 0) {
            if (null == client.getClientSecret()) {
                client.setClientSecret(encoder.encode(""));
            } else {
                client.setClientSecret(encoder.encode(client.getClientSecret().trim()));
            }
            return client;
        } else {
            throw new ClientAlreadyExistException();
        }
    }

    /**
     * selected resource ids should be eligible resource
     */
    public static void validateResourceId(BizClient client, AppBizClientApplicationService appBizClientApplicationService) throws IllegalArgumentException {
        if (client.getResourceIds() == null || client.getResourceIds().size() == 0)
            throw new IllegalArgumentException("invalid resourceId found");
        String join = String.join(".", client.getResourceIds());
        SumPagedRep<AppBizClientCardRep> appBizClientCardRepSumPagedRep = appBizClientApplicationService.readByQuery("id:" + join, null, null);
        if (appBizClientCardRepSumPagedRep.getData().size() != client.getResourceIds().size())
            throw new IllegalArgumentException("unable to find resourceId listed");
        if (appBizClientCardRepSumPagedRep.getData().stream().anyMatch(e -> !e.getResourceIndicator()))
            throw new IllegalArgumentException("invalid resourceId found");
    }

    /**
     * if client is marked as resource then it must be a backend and first party application
     */
    public static void validateResourceIndicator(BizClient client) throws IllegalArgumentException {
        if (client.getResourceIndicator())
            if (client.getGrantedAuthorities().stream().noneMatch(e -> e.equals(BizClientAuthorityEnum.ROLE_BACKEND))
                    || client.getGrantedAuthorities().stream().noneMatch(e -> e.equals(BizClientAuthorityEnum.ROLE_FIRST_PARTY)))
                throw new IllegalArgumentException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
    }

    @Override
    public String getClientId() {
        return id.toString();
    }

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
        return grantedAuthorities.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toList());
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

    public BizClient replace(UpdateClientCommand command, RevokeBizClientTokenService tokenRevocationService, AppBizClientApplicationService appBizClientApplicationService, BCryptPasswordEncoder encoder) {
        shouldRevoke(command, tokenRevocationService);
        validateResourceId(this, appBizClientApplicationService);
        if (StringUtils.hasText(command.getClientSecret())) {
            this.setClientSecret(encoder.encode(command.getClientSecret()));
        }
        this.grantTypeEnums = command.getGrantTypeEnums();
        this.description = command.getDescription();
        this.grantedAuthorities = command.getGrantedAuthorities();
        this.scopeEnums = command.getScopeEnums();
        this.accessTokenValiditySeconds = command.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = command.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = command.getRefreshTokenValiditySeconds();
        this.resourceIds = command.getResourceIds();
        this.resourceIndicator = command.getResourceIndicator();
        this.autoApprove = command.getAutoApprove();
        this.hasSecret = command.getClientSecret() != null;
        this.name = command.getName();
        validateResourceIndicator(this);
        return this;
    }

    /**
     * root client can not be deleted
     */
    public void validateDelete() {
        if (this.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new RootClientDeleteException();
    }

    public void shouldRevoke(UpdateClientCommand newClient, RevokeBizClientTokenService tokenRevocationService) {

        if (StringUtils.hasText(newClient.getClientSecret())
                || authorityChanged(this, newClient)
                || scopeChanged(this, newClient)
                || accessTokenChanged(this, newClient)
                || refreshTokenChanged(this, newClient)
                || grantTypeChanged(this, newClient)
                || resourceIdChanged(this, newClient)
                || redirectUrlChanged(this, newClient)

        ) {
            tokenRevocationService.blacklist(this.getId());
        }
    }

    private boolean authorityChanged(BizClient oldClient, UpdateClientCommand newClient) {
        return !oldClient.getGrantedAuthorities().equals(newClient.getGrantedAuthorities());
    }

    private boolean scopeChanged(BizClient oldClient, UpdateClientCommand newClient) {
        return !oldClient.getScopeEnums().equals(newClient.getScopeEnums());
    }

    /**
     * access token validity seconds can not be null
     *
     * @param oldClient
     * @param newClient
     * @return
     */
    private boolean accessTokenChanged(BizClient oldClient, UpdateClientCommand newClient) {
        return !oldClient.getAccessTokenValiditySeconds().equals(newClient.getAccessTokenValiditySeconds());
    }

    private boolean refreshTokenChanged(BizClient oldClient, UpdateClientCommand newClient) {
        if (oldClient.getRefreshTokenValiditySeconds() == null && newClient.getRefreshTokenValiditySeconds() == null) {
            return false;
        } else if (oldClient.getRefreshTokenValiditySeconds() != null && oldClient.getRefreshTokenValiditySeconds().equals(newClient.getRefreshTokenValiditySeconds())) {
            return false;
        } else if (newClient.getRefreshTokenValiditySeconds() != null && newClient.getRefreshTokenValiditySeconds().equals(oldClient.getRefreshTokenValiditySeconds())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean grantTypeChanged(BizClient oldClient, UpdateClientCommand newClient) {
        return !oldClient.getGrantTypeEnums().equals(newClient.getGrantTypeEnums());
    }

    private boolean redirectUrlChanged(BizClient oldClient, UpdateClientCommand newClient) {
        if ((oldClient.getRegisteredRedirectUri() == null || oldClient.getRegisteredRedirectUri().isEmpty())
                && (newClient.getRegisteredRedirectUri() == null || newClient.getRegisteredRedirectUri().isEmpty())) {
            return false;
        } else if (oldClient.getRegisteredRedirectUri() != null && oldClient.getRegisteredRedirectUri().equals(newClient.getRegisteredRedirectUri())) {
            return false;
        } else if (newClient.getRegisteredRedirectUri() != null && newClient.getRegisteredRedirectUri().equals(oldClient.getRegisteredRedirectUri())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean resourceIdChanged(BizClient oldClient, UpdateClientCommand newClient) {
        return !oldClient.getResourceIds().equals(newClient.getResourceIds());
    }

}
