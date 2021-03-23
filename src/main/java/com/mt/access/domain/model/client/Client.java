package com.mt.access.domain.model.client;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.event.*;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Table
@Entity
@NoArgsConstructor
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Client extends Auditable {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private ClientId clientId;
    @Getter
    private String name;
    @Getter
    private String secret;
    @Getter
    private String description;

    @Convert(converter = Role.DBConverter.class)
    @Getter
    @Column(name = "authorities")
    private Set<Role> roles;

    @Convert(converter = Scope.DBConverter.class)
    @Getter
    @Column(name = "scopes")
    private Set<Scope> scopes;

    /**
     * if lazy then loadClientByClientId needs to be transactional
     * use eager as @Transactional is adding too much overhead
     */
    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "resources_map",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id", "domainId"})
    )
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(updatable = false, nullable = false))
    })
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private final Set<ClientId> resources = new HashSet<>();

    @Getter
    @Column(name = "accessible_")
    private boolean accessible = false;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    private RedirectDetail authorizationCodeGrant;
    @Convert(converter = GrantType.DBConverter.class)
    @Getter
    private Set<GrantType> grantTypes;

    @Embedded
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private TokenDetail tokenDetail;

    private void setGrantTypes(Set<GrantType> grantTypes) {
        if (grantTypes.contains(GrantType.REFRESH_TOKEN) && !grantTypes.contains(GrantType.PASSWORD))
            Validator.handleError("refresh token grant requires password grant");
        this.grantTypes = grantTypes;
    }

    private void setName(String name) {
        Validator.notNull(name);
        String trim = name.trim();
        Validator.notBlank(trim);
        Validator.lengthGreaterThanOrEqualTo(trim, 1);
        Validator.lengthLessThanOrEqualTo(trim, 50);
        Validator.whitelistOnly(trim);
        this.name = trim;
    }

    private void setDescription(String description) {
        if (description != null) {
            String trim = description.trim();
            Validator.lengthLessThanOrEqualTo(trim, 50);
            Validator.whitelistOnly(trim);
            this.description = description;
        }
    }

    private void setScopes(Set<Scope> scopes) {
        Validator.notEmpty(scopes);
        Validator.noNullMember(scopes);
        this.scopes.clear();
        this.scopes.addAll(scopes);
    }

    private void setRoles(Set<Role> roles) {
        Validator.notEmpty(roles);
        Validator.noNullMember(roles);
        this.roles.clear();
        this.roles.addAll(roles);
    }

    private void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public void removeResource(ClientId clientId) {
        this.resources.remove(clientId);
    }

    private void setResources(Set<ClientId> resources) {
        Validator.notNull(resources);
        if (!resources.equals(this.resources)) {
            this.resources.clear();
            this.resources.addAll(resources);
            DomainRegistry.getClientValidationService().validate(this, new HttpValidationNotificationHandler());
        }
    }

    public boolean removable() {
        return this.roles.stream().noneMatch(Role.ROLE_ROOT::equals);
    }

    public Client(ClientId clientId,
                  String name,
                  @Nullable String secret,
                  String description,
                  boolean accessible,
                  Set<Scope> scopes,
                  Set<Role> roles,
                  Set<ClientId> resources,
                  Set<GrantType> grantTypes,
                  TokenDetail tokenDetail,
                  RedirectDetail authorizationCodeGrant
    ) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setClientId(clientId);
        setResources(resources);
        setScopes(scopes);
        setDescription(description);
        setRoles(roles);
        setAccessible(accessible);
        setName(name);
        setSecret(secret);
        setGrantTypes(grantTypes);
        setTokenDetail(tokenDetail);
        setAuthorizationCodeGrant(authorizationCodeGrant);
        DomainEventPublisher.instance().publish(new ClientCreated(clientId));
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getClientRepository().add(this);
    }

    public void replace(String name,
                        String secret,
                        String description,
                        boolean accessible,
                        Set<Scope> scopes,
                        Set<Role> roles,
                        Set<ClientId> resources,
                        Set<GrantType> grantTypes,
                        TokenDetail tokenDetail,
                        RedirectDetail authorizationCodeGrant
    ) {
        if (scopesChanged(scopes)) {
            DomainEventPublisher.instance().publish(new ClientScopesChanged(clientId));
        }
        if (resourcesChanged(resources)) {
            DomainEventPublisher.instance().publish(new ClientResourcesChanged(clientId));
        }
        if (tokenDetailChanged(tokenDetail)) {
            DomainEventPublisher.instance().publish(new ClientTokenDetailChanged(clientId));
        }
        if (rolesChanged(roles)) {
            DomainEventPublisher.instance().publish(new ClientAuthoritiesChanged(clientId));
        }
        if (this.accessible && !accessible) {
            DomainEventPublisher.instance().publish(new ClientAccessibilityRemoved(clientId));
        }
        if (secretChanged(secret)) {
            DomainEventPublisher.instance().publish(new ClientSecretChanged(clientId));
        }
        if (!ObjectUtils.equals(grantTypes, this.grantTypes)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
        }
        setScopes(scopes);
        setResources(resources);
        setRoles(roles);
        setAccessible(accessible);
        setSecret(secret);
        setGrantTypes(grantTypes);
        setTokenDetail(tokenDetail);
        setName(name);
        setDescription(description);
        setAuthorizationCodeGrant(authorizationCodeGrant);
        validate(new HttpValidationNotificationHandler());
    }

    @PreUpdate
    private void preUpdate() {
        DomainEventPublisher.instance().publish(new ClientUpdated(getClientId()));
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new ClientValidator(this, handler)).validate();
    }

    public Endpoint addNewEndpoint(Set<String> userRoles, Set<String> clientRoles, Set<String> scopes, String description, String path, EndpointId endpointId, String method, boolean secured, boolean userOnly, boolean clientOnly) {
        return new Endpoint(getClientId(), userRoles, clientRoles, scopes, description, path, endpointId, method, secured, userOnly, clientOnly);
    }

    private void setSecret(String secret) {
        if (StringUtils.hasText(secret))
            this.secret = DomainRegistry.getEncryptionService().encryptedValue(secret);
    }

    private boolean secretChanged(String secret) {
        return StringUtils.hasText(secret);
    }

    public int accessTokenValiditySeconds() {
        return tokenDetail.getAccessTokenValiditySeconds();
    }

    private boolean resourcesChanged(Set<ClientId> clientIds) {
        return !ObjectUtils.equals(this.resources, clientIds);
    }

    private boolean rolesChanged(Set<Role> authorities) {
        return !ObjectUtils.equals(this.roles, authorities);
    }

    private boolean scopesChanged(Set<Scope> scopes) {
        return !ObjectUtils.equals(this.scopes, scopes);
    }

    private boolean tokenDetailChanged(TokenDetail tokenDetail) {
        return !ObjectUtils.equals(this.tokenDetail, scopes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return Objects.equal(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), clientId);
    }

    public void removeAllReferenced() {
        DomainEventPublisher.instance().publish(new ClientDeleted(clientId));
        if (isAccessible()) {
            DomainEventPublisher.instance().publish(new ClientAsResourceDeleted(clientId));
        }
    }

    public int getRefreshTokenValiditySeconds() {
        if (grantTypes.contains(GrantType.PASSWORD) && grantTypes.contains(GrantType.REFRESH_TOKEN)) {
            return getTokenDetail().getRefreshTokenValiditySeconds();
        }
        return 0;
    }

    public boolean getAutoApprove() {
        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            return getAuthorizationCodeGrant().isAutoApprove();
        }
        return false;
    }

    public Set<String> getRegisteredRedirectUri() {
        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            return getAuthorizationCodeGrant().getRedirectUrls().stream().map(RedirectURL::getValue).collect(Collectors.toSet());
        }
        return Collections.emptySet();

    }
}
