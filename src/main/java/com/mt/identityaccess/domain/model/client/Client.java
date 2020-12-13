package com.mt.identityaccess.domain.model.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.hw.config.DomainEventPublisher;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.Aggregate;
import com.hw.shared.sql.SumPagedRep;
import com.mt.identityaccess.application.AppBizClientApplicationService;
import com.mt.identityaccess.application.command.ProvisionClientCommand;
import com.mt.identityaccess.application.command.ReplaceClientCommand;
import com.mt.identityaccess.application.representation.AppBizClientCardRep;
import com.mt.identityaccess.domain.model.RevokeTokenService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * use different field name to make it more flexible also avoid copy properties type mismatch
 * e.g getting return string instead of enum
 */
@Entity
@Table
@Data
@NoArgsConstructor
@Where(clause = "deleted = false")
public class Client extends Auditable implements Aggregate {
    public static final String ENTITY_ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String ENTITY_RESOURCE_INDICATOR = "resourceIndicator";
    public static final String ENTITY_NAME = "name";
    public static final String ENTITY_GRANT_TYPE_ENUMS = "grantTypeEnums";
    public static final String ENTITY_GRANT_AUTHORITIES = "grantedAuthorities";
    public static final String ENTITY_SCOPE_ENUMS = "scopeEnums";
    public static final String ENTITY_RESOURCE_IDS = "resourceIds";
    @Id
    private Long id;
    private ClientId clientId;
    private String description;
    @Column
    @Nullable
    private String name;
    @Nullable
    @Column
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

    @JsonIgnore
    @ManyToMany(mappedBy = "following")
    private Set<Client> followers = new HashSet<>();

    @ManyToMany
    private Set<Client> following = new HashSet<>();
    /**
     * indicates if a client is a resource, if true resource id will default to client id
     */
    @Column
    @Nullable
    private boolean resourceIndicator = false;
    /**
     * indicates if a authorization_code client can be auto approved
     */
    @Column
    @Nullable
    private boolean autoApprove = false;
    /**
     * this field is not used in spring oauth2,
     * client with no secret requires empty secret (mostly encoded)
     * below is empty string "" encoded, use if needed
     * $2a$10$KRp4.vK8F8MYLJGEz7im8.71T2.vFQj/rrNLQLOLPEADuv0Gdg.x6
     */
    @Column
    @NotNull
    private boolean hasSecret = false;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    private Client(long id, ProvisionClientCommand command, CrudRepository<Client, Long> repo) {
        this.id = id;
        this.clientSecret = command.getClientSecret();
        this.description = command.getDescription();
        this.grantTypeEnums = command.getGrantTypeEnums();
        this.grantedAuthorities = command.getGrantedAuthorities();
        this.scopeEnums = command.getScopeEnums();
        this.accessTokenValiditySeconds = command.getAccessTokenValiditySeconds();
        this.registeredRedirectUri = command.getRegisteredRedirectUri();
        this.refreshTokenValiditySeconds = command.getRefreshTokenValiditySeconds();
        this.following = new HashSet<>();
        repo.findAllById(command.getResourceIds().stream().map(Long::parseLong).collect(Collectors.toSet())).forEach(e -> {
            this.following.add(e);
        });
        this.resourceIndicator = command.getResourceIndicator();
        this.autoApprove = command.getAutoApprove();
        this.hasSecret = command.isHasSecret();
        this.name = command.getName();
    }

    public Client(ClientId nextIdentity, BasicClientDetail basicClientDetail, ClientCredentialsGrantDetail clientRepository, PasswordGrantDetail passwordGrantDetail, RefreshTokenGrantDetail refreshTokenGrantDetail, AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {

    }

    public static Client create(long id, ProvisionClientCommand command, AppBizClientApplicationService appBizClientApplicationService, BCryptPasswordEncoder encoder, CrudRepository<Client, Long> repo) {
        Client client = new Client(id, command, repo);
        validateResourceId(client);
        validateResourceIndicator(client);
        SumPagedRep<AppBizClientCardRep> appBizClientCardRepSumPagedRep = appBizClientApplicationService.readByQuery("id:" + client.getId(), null, null);
        if (appBizClientCardRepSumPagedRep.getData().isEmpty()) {
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
     * selected resource ids should be eligible resource, nullable
     */
    public static void validateResourceId(Client client) {
        if (client.getFollowing() != null && !client.getFollowing().isEmpty()) {
            if (client.getFollowing().stream().anyMatch(e -> !e.isResourceIndicator()))
                throw new IllegalArgumentException("invalid resourceId found");
        }
    }

    /**
     * if client is marked as resource then it must be a backend and first party application
     */
    public static void validateResourceIndicator(Client client) {
        if (Boolean.TRUE.equals(client.isResourceIndicator()) && (client.getGrantedAuthorities().stream().noneMatch(e -> e.equals(BizClientAuthorityEnum.ROLE_BACKEND))
                || client.getGrantedAuthorities().stream().noneMatch(e -> e.equals(BizClientAuthorityEnum.ROLE_FIRST_PARTY))))
            throw new IllegalArgumentException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
    }

    public Client replace(ReplaceClientCommand command, RevokeTokenService tokenRevocationService, BCryptPasswordEncoder encoder, CrudRepository<Client, Long> repo) {
        shouldRevoke(command, tokenRevocationService);
        validateResourceId(this);
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
        this.following = new HashSet<>();
        repo.findAllById(command.getResourceIds().stream().map(Long::parseLong).collect(Collectors.toSet())).forEach(e -> {
            this.following.add(e);
        });
        this.resourceIndicator = command.getResourceIndicator();
        this.autoApprove = command.getAutoApprove();
        this.hasSecret = command.isHasSecret();
        this.name = command.getName();
        validateResourceIndicator(this);
        return this;
    }

    /**
     * root client can not be deleted
     */
    public void validateDelete() {
        if (this.getGrantedAuthorities().stream().anyMatch(BizClientAuthorityEnum.ROLE_ROOT::equals))
            throw new RootClientDeleteException();
    }

    public void shouldRevoke(ReplaceClientCommand newClient, RevokeTokenService tokenRevocationService) {

        if (StringUtils.hasText(newClient.getClientSecret())
                || authorityChanged(this, newClient)
                || scopeChanged(this, newClient)
                || accessTokenChanged(this, newClient)
                || refreshTokenChanged(this, newClient)
                || grantTypeChanged(this, newClient)
                || resourceIdChanged(this, newClient)
                || redirectUrlChanged(this, newClient)

        ) {
            tokenRevocationService.revokeClientToken(getClientId());
        }
    }

    private boolean authorityChanged(Client oldClient, ReplaceClientCommand newClient) {
        return !oldClient.getGrantedAuthorities().equals(newClient.getGrantedAuthorities());
    }

    private boolean scopeChanged(Client oldClient, ReplaceClientCommand newClient) {
        return !oldClient.getScopeEnums().equals(newClient.getScopeEnums());
    }

    /**
     * access token validity seconds can not be null
     *
     * @param oldClient
     * @param newClient
     * @return
     */
    private boolean accessTokenChanged(Client oldClient, ReplaceClientCommand newClient) {
        return !oldClient.getAccessTokenValiditySeconds().equals(newClient.getAccessTokenValiditySeconds());
    }

    private boolean refreshTokenChanged(Client oldClient, ReplaceClientCommand newClient) {
        if (oldClient.getRefreshTokenValiditySeconds() == null && newClient.getRefreshTokenValiditySeconds() == null) {
            return false;
        } else if (oldClient.getRefreshTokenValiditySeconds() != null && oldClient.getRefreshTokenValiditySeconds().equals(newClient.getRefreshTokenValiditySeconds())) {
            return false;
        } else
            return newClient.getRefreshTokenValiditySeconds() == null || !newClient.getRefreshTokenValiditySeconds().equals(oldClient.getRefreshTokenValiditySeconds());
    }

    private boolean grantTypeChanged(Client oldClient, ReplaceClientCommand newClient) {
        return !oldClient.getGrantTypeEnums().equals(newClient.getGrantTypeEnums());
    }

    private boolean redirectUrlChanged(Client oldClient, ReplaceClientCommand newClient) {
        if ((oldClient.getRegisteredRedirectUri() == null || oldClient.getRegisteredRedirectUri().isEmpty())
                && (newClient.getRegisteredRedirectUri() == null || newClient.getRegisteredRedirectUri().isEmpty())) {
            return false;
        } else if (oldClient.getRegisteredRedirectUri() != null && oldClient.getRegisteredRedirectUri().equals(newClient.getRegisteredRedirectUri())) {
            return false;
        } else
            return newClient.getRegisteredRedirectUri() == null || !newClient.getRegisteredRedirectUri().equals(oldClient.getRegisteredRedirectUri());
    }

    private boolean resourceIdChanged(Client oldClient, ReplaceClientCommand newClient) {
        return !oldClient.getFollowing().stream().map(e -> e.getId().toString()).collect(Collectors.toSet()).equals(newClient.getResourceIds());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client bizClient = (Client) o;
        return Objects.equal(id, bizClient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public ClientId clientId(){
        return clientId;
    }

    public void replace(BasicClientDetail basicClientDetail, ClientCredentialsGrantDetail clientCredentialsGrantDetail, PasswordGrantDetail passwordGrantDetail, RefreshTokenGrantDetail refreshTokenGrantDetail, AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    public boolean allowRemoval() {
        return true;
    }

    public void replace(BasicClientDetail basicClientDetail, ClientCredentialsGrantDetail clientCredentialsGrantDetail, PasswordGrantDetail passwordGrantDetail) {
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }
}
