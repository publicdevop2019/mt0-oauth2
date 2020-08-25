package com.hw.aggregate.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.aggregate.client.BizClientRepo;
import com.hw.aggregate.client.RevokeBizClientTokenService;
import com.hw.aggregate.client.command.CreateClientCommand;
import com.hw.aggregate.client.command.UpdateClientCommand;
import com.hw.aggregate.client.exception.ClientAlreadyExistException;
import com.hw.aggregate.client.exception.RootClientDeleteException;
import com.hw.shared.Auditable;
import com.hw.shared.BadRequestException;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.IdBasedEntity;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * use different field name to make it more flexible also avoid copy properties type mismatch
 * e.g getting return string instead of enum
 */
@Entity
@Table
@Data
public class BizClient extends Auditable implements ClientDetails, IdBasedEntity {
    public static final String ENTITY_CLIENT_ID = "clientId";
    public static final String ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String ENTITY_RESOURCE_INDICATOR = "resourceIndicator";
    @Id
    private Long id;
    @NotNull
    @Column(nullable = false)
    private String clientId;
    private String description;
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
    private List<@Valid @NotNull GrantedAuthorityImpl<BizClientAuthorityEnum>> grantedAuthorities;
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
    @NotEmpty
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
        this.clientId = command.getClientId();
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
    }

    public static BizClient create(long id, CreateClientCommand command, BizClientRepo clientRepo, BCryptPasswordEncoder encoder) {
        BizClient client = new BizClient(id, command);
        validateResourceId(client, clientRepo);
        validateResourceIndicator(client);
        Optional<BizClient> clientId = clientRepo.findByClientId(client.getClientId());
        if (clientId.isEmpty()) {
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
    private static void validateResourceId(BizClient client, BizClientRepo clientRepo) throws IllegalArgumentException {
        if (client.getResourceIds() == null || client.getResourceIds().size() == 0
                || client.getResourceIds().stream().anyMatch(resourceId -> clientRepo.findByClientId(resourceId).isEmpty()
                || !clientRepo.findByClientId(resourceId).get().getResourceIndicator()))
            throw new BadRequestException("invalid resourceId found");
    }

    /**
     * if client is marked as resource then it must be a backend and first party application
     */
    private static void validateResourceIndicator(BizClient client) throws IllegalArgumentException {
        if (client.getResourceIndicator())
            if (client.getGrantedAuthorities().stream().noneMatch(e -> e.getGrantedAuthority().equals(BizClientAuthorityEnum.ROLE_BACKEND))
                    || client.getGrantedAuthorities().stream().noneMatch(e -> e.getGrantedAuthority().equals(BizClientAuthorityEnum.ROLE_FIRST_PARTY)))
                throw new BadRequestException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
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

    public BizClient replace(UpdateClientCommand command, RevokeBizClientTokenService tokenRevocationService, BizClientRepo clientRepo, BCryptPasswordEncoder encoder) {
        boolean b = shouldRevoke(this, command);
        if (b)
            tokenRevocationService.blacklist(this.getClientId());
        if (StringUtils.hasText(command.getClientSecret())) {
            this.setClientSecret(encoder.encode(command.getClientSecret()));
        }
        this.clientId = command.getClientId();
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
        validateResourceIndicator(this);
        validateResourceId(this, clientRepo);
        return this;
    }

    /**
     * root client can not be deleted
     */
    public boolean preventRootChange() {
        if (this.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new RootClientDeleteException();
        return true;
    }

    public boolean shouldRevoke(BizClient oldClient, UpdateClientCommand newClient) {
        if (!newClient.getClientId().equals(oldClient.getClientId())) {
            return true;
        } else if (StringUtils.hasText(newClient.getClientSecret())) {
            return true;
        } else if (authorityChanged(oldClient, newClient)) {
            return true;
        } else if (scopeChanged(oldClient, newClient)) {
            return true;
        } else if (accessTokenChanged(oldClient, newClient)) {
            return true;
        } else if (refreshTokenChanged(oldClient, newClient)) {
            return true;
        } else if (grantTypeChanged(oldClient, newClient)) {
            return true;
        } else if (resourceIdChanged(oldClient, newClient)) {
            return true;
        } else if (redirectUrlChanged(oldClient, newClient)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean authorityChanged(BizClient oldClient, UpdateClientCommand newClient) {
        HashSet<GrantedAuthorityImpl<BizClientAuthorityEnum>> grantedAuthorities = new HashSet<>(oldClient.getGrantedAuthorities());
        HashSet<GrantedAuthorityImpl<BizClientAuthorityEnum>> grantedAuthorities2 = new HashSet<>(newClient.getGrantedAuthorities());
        return !grantedAuthorities.equals(grantedAuthorities2);
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
