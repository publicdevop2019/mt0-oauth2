package com.mt.identityaccess.domain.model.client;

import com.google.common.base.Objects;
import com.mt.common.audit.Auditable;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.identityaccess.application.client.ClientQuery;
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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
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
    @Setter
    @Getter
    private String name;
    @Getter
    private String secret;
    @Setter
    @Getter
    private String description;

    @Convert(converter = Authority.AuthorityConverter.class)
    @Getter
    private final Set<Authority> authorities = EnumSet.noneOf(Authority.class);

    @Convert(converter = Scope.ScopeConverter.class)
    @Getter
    private final Set<Scope> scopes = EnumSet.noneOf(Scope.class);

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)//if lazy then loadClientByClientId needs to be transactional
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

    @Setter(AccessLevel.PRIVATE)
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

    @Getter
    @Setter(AccessLevel.NONE)
    @Version
    private Integer version;

    public void setScopes(Set<Scope> scopes) {
        this.scopes.clear();
        this.scopes.addAll(scopes);
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities.clear();
        this.authorities.addAll(authorities);
        if (accessible) {
            if (
                    authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_BACKEND))
                            || authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_FIRST_PARTY))
            ) {
                throw new IllegalArgumentException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
            }
            setAccessible(true);
        } else {
            setAccessible(false);
        }
    }

    public void setAccessible(boolean accessible, Set<Authority> authorities) {
        if (accessible) {
            if (
                    authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_BACKEND))
                            || authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_FIRST_PARTY))
            )
                throw new IllegalArgumentException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
        }
        this.accessible = accessible;
    }

    public void setResources(Set<ClientId> resources) {
            if (!resources.equals(this.resources)) {
                if (!resources.isEmpty()) {
                    List<Client> clientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(resources));
                    if (clientsOfQuery.size() != resources.size()) {
                        throw new IllegalArgumentException("invalid resource(s) found");
                    }
                    boolean b = clientsOfQuery.stream().anyMatch(e -> !e.accessible);
                    if (b) {
                        throw new IllegalArgumentException("invalid resource(s) found");
                    }
                }
                this.resources.clear();
                this.resources.addAll(resources);
            }
    }

    public boolean isNonRoot() {
        return this.authorities.stream().noneMatch(Authority.ROLE_ROOT::equals);
    }

    public Client(ClientId nextIdentity,
                  String name,
                  String secret,
                  String description,
                  boolean accessible,
                  Set<Scope> scopes,
                  Set<Authority> authorities,
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
        setAuthorities(authorities);
        setAccessible(accessible, authorities);
        setName(name);
        setSecret(secret);
        setClientCredentialsGrant(clientCredentialsGrant);
        setPasswordGrant(passwordGrant);
        setAuthorizationCodeGrant(authorizationCodeGrant);
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
                        Set<Authority> authorities,
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
        if (accessibleChanged(accessible)) {
            DomainEventPublisher.instance().publish(new ClientAccessibleChanged(clientId));
        }
        setResources(resources);
        setScopes(scopes);
        setDescription(description);
        setAccessible(accessible);
        setAuthorities(authorities);
        setName(name);
        ClientCredentialsGrant.detectChange(this.getClientCredentialsGrant(), clientCredentialsGrant, getClientId());
        setClientCredentialsGrant(clientCredentialsGrant);
        PasswordGrant.detectChange(this.getPasswordGrant(), passwordGrant, getClientId());
        setPasswordGrant(passwordGrant);
        DomainEventPublisher.instance().publish(new ClientUpdated(getClientId()));
    }

    public void replace(String name,
                        String secret,
                        String description,
                        boolean accessible,
                        Set<Scope> scopes,
                        Set<Authority> authorities,
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
            DomainEventPublisher.instance().publish(new ClientAccessibleChanged(clientId));
        }
        if (secretChanged(secret)) {
            DomainEventPublisher.instance().publish(new ClientSecretChanged(clientId));
        }
        setResources(resources);
        setScopes(scopes);
        setDescription(description);
        setAccessible(accessible);
        setAuthorities(authorities);
        setName(name);
        if (StringUtils.hasText(secret))
            setSecret(secret);
        ClientCredentialsGrant.detectChange(this.getClientCredentialsGrant(), clientCredentialsGrant, getClientId());
        setClientCredentialsGrant(clientCredentialsGrant);
        PasswordGrant.detectChange(this.getPasswordGrant(), passwordGrant, getClientId());
        setPasswordGrant(passwordGrant);
        AuthorizationCodeGrant.detectChange(this.getAuthorizationCodeGrant(), authorizationCodeGrant, getClientId());
        setAuthorizationCodeGrant(authorizationCodeGrant);
    }

    @PreUpdate
    private void preUpdate(){
        DomainEventPublisher.instance().publish(new ClientUpdated(getClientId()));
    }

    public Endpoint addNewEndpoint(String expression, String description, String path, EndpointId endpointId, String method) {
        return new Endpoint(getClientId(), expression, description, path, endpointId, method);
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

    private boolean authoritiesChanged(Set<Authority> authorities) {
        return !ObjectUtils.equals(this.authorities, authorities);
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
}
