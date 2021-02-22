package com.mt.identityaccess.domain.model.client;

import com.google.common.base.Objects;
import com.mt.common.audit.Auditable;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.validate.HttpValidationNotificationHandler;
import com.mt.common.validate.ValidationNotificationHandler;
import com.mt.common.validate.Validator;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.event.*;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.domain.model.endpoint.EndpointId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<Role> roles = EnumSet.noneOf(Role.class);

    @Convert(converter = Scope.DBConverter.class)
    @Getter
    @Column(name = "scopes")
    private final Set<Scope> scopes = EnumSet.noneOf(Scope.class);

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
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "client_credentials_gt_enabled")),
            @AttributeOverride(name = "accessTokenValiditySeconds", column = @Column(name = "client_credentials_gt_access_token_validity_seconds"))
    })
    private ClientCredentialsGrant clientCredentialsGrant;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "password_gt_enabled")),
            @AttributeOverride(name = "accessTokenValiditySeconds", column = @Column(name = "password_gt_access_token_validity_seconds"))
    })
    private PasswordGrant passwordGrant;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "authorization_code_gt_enabled")),
            @AttributeOverride(name = "accessTokenValiditySeconds", column = @Column(name = "authorization_code_gt_access_token_validity_seconds"))
    })
    private AuthorizationCodeGrant authorizationCodeGrant;

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
        if (this.accessible && !accessible) {
            DomainEventPublisher.instance().publish(new ClientAccessibilityRemoved(clientId));
        }
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
            DomainRegistry.clientValidationService().validate(this, new HttpValidationNotificationHandler());
        }
    }

    public boolean removable() {
        return this.roles.stream().noneMatch(Role.ROLE_ROOT::equals);
    }

    public Client(ClientId nextIdentity,
                  String name,
                  String secret,
                  String description,
                  boolean accessible,
                  Set<Scope> scopes,
                  Set<Role> roles,
                  Set<ClientId> resources,
                  ClientCredentialsGrant clientCredentialsGrant,
                  PasswordGrant passwordGrant,
                  AuthorizationCodeGrant authorizationCodeGrant
    ) {
        setId(DomainRegistry.uniqueIdGeneratorService().id());
        setClientId(nextIdentity);
        setResources(resources);
        setScopes(scopes);
        setDescription(description);
        setRoles(roles);
        setAccessible(accessible);
        setName(name);
        setSecret(secret);
        setClientCredentialsGrant(clientCredentialsGrant);
        setPasswordGrant(passwordGrant);
        setAuthorizationCodeGrant(authorizationCodeGrant);
        validate(new HttpValidationNotificationHandler());
    }

    public Set<GrantType> totalGrantTypes() {
        HashSet<GrantType> grantTypes = new HashSet<>();
        if (clientCredentialsGrant != null && clientCredentialsGrant.isEnabled()) {
            grantTypes.add(clientCredentialsGrant.name());
        }
        if (passwordGrant != null && passwordGrant.isEnabled()) {
            grantTypes.add(passwordGrant.name());
            if (passwordGrant.getRefreshTokenGrant() != null && passwordGrant.getRefreshTokenGrant().isEnabled()) {
                grantTypes.add(RefreshTokenGrant.NAME);
            }
        }
        if (authorizationCodeGrant != null && authorizationCodeGrant.isEnabled()) {
            grantTypes.add(authorizationCodeGrant.name());
        }
        return grantTypes;
    }

    public void replace(String name,
                        String description,
                        boolean accessible,
                        Set<Scope> scopes,
                        Set<Role> authorities,
                        Set<ClientId> resources,
                        ClientCredentialsGrant clientCredentialsGrant,
                        PasswordGrant passwordGrant
    ) {
        if (authoritiesChanged(authorities)) {
            DomainEventPublisher.instance().publish(new ClientAuthoritiesChanged(clientId));
        }
        if (scopesChanged(scopes)) {
            DomainEventPublisher.instance().publish(new ClientScopesChanged(clientId));
        }
        if (resourcesChanged(resources)) {
            DomainEventPublisher.instance().publish(new ClientResourcesChanged(clientId));
        }
        setResources(resources);
        setScopes(scopes);
        setDescription(description);
        setAccessible(accessible);
        setRoles(authorities);
        setName(name);
        ClientCredentialsGrant.detectChange(this.getClientCredentialsGrant(), clientCredentialsGrant, getClientId());
        setClientCredentialsGrant(clientCredentialsGrant);
        PasswordGrant.detectChange(this.getPasswordGrant(), passwordGrant, getClientId());
        setPasswordGrant(passwordGrant);
        validate(new HttpValidationNotificationHandler());
    }

    public void replace(String name,
                        String secret,
                        String description,
                        boolean accessible,
                        Set<Scope> scopes,
                        Set<Role> authorities,
                        Set<ClientId> resources,
                        ClientCredentialsGrant clientCredentialsGrant,
                        PasswordGrant passwordGrant,
                        AuthorizationCodeGrant authorizationCodeGrant
    ) {
        if (authoritiesChanged(authorities)) {
            DomainEventPublisher.instance().publish(new ClientAuthoritiesChanged(clientId));
        }
        if (scopesChanged(scopes)) {
            DomainEventPublisher.instance().publish(new ClientScopesChanged(clientId));
        }
        if (resourcesChanged(resources)) {
            DomainEventPublisher.instance().publish(new ClientResourcesChanged(clientId));
        }
        if (accessibleChanged(accessible)) {
            DomainEventPublisher.instance().publish(new ClientAccessibilityRemoved(clientId));
        }
        if (secretChanged(secret)) {
            DomainEventPublisher.instance().publish(new ClientSecretChanged(clientId));
        }
        setResources(resources);
        setScopes(scopes);
        setDescription(description);
        setAccessible(accessible);
        setRoles(authorities);
        setName(name);
        if (StringUtils.hasText(secret))
            setSecret(secret);
        ClientCredentialsGrant.detectChange(this.getClientCredentialsGrant(), clientCredentialsGrant, getClientId());
        setClientCredentialsGrant(clientCredentialsGrant);
        PasswordGrant.detectChange(this.getPasswordGrant(), passwordGrant, getClientId());
        setPasswordGrant(passwordGrant);
        AuthorizationCodeGrant.detectChange(this.getAuthorizationCodeGrant(), authorizationCodeGrant, getClientId());
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
        this.secret = DomainRegistry.encryptionService().encryptedValue(secret);
    }

    private boolean secretChanged(String secret) {
        return StringUtils.hasText(secret);
    }

    public int accessTokenValiditySeconds() {
        if (getClientCredentialsGrant() != null) {
            return getClientCredentialsGrant().getAccessTokenValiditySeconds();
        } else if (getPasswordGrant() != null) {
            return getPasswordGrant().getAccessTokenValiditySeconds();
        } else if (getAuthorizationCodeGrant() != null) {
            return getAuthorizationCodeGrant().getAccessTokenValiditySeconds();
        } else {
            return 0;
        }

    }

    private boolean resourcesChanged(Set<ClientId> clientIds) {
        return !ObjectUtils.equals(this.resources, clientIds);
    }

    private boolean accessibleChanged(boolean b) {
        return isAccessible() != b;
    }

    private boolean authoritiesChanged(Set<Role> authorities) {
        return !ObjectUtils.equals(this.roles, authorities);
    }

    private boolean scopesChanged(Set<Scope> scopes) {
        return !ObjectUtils.equals(this.scopes, scopes);
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
}
