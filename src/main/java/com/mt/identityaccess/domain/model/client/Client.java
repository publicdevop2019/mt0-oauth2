package com.mt.identityaccess.domain.model.client;

import com.mt.common.Auditable;
import com.mt.identityaccess.application.client.ClientQuery;
import com.mt.identityaccess.config.DomainEventPublisher;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.UniqueIdGeneratorService;
import com.mt.identityaccess.domain.model.client.event.*;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Where;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table
@Entity
@Setter
@Where(clause = "deleted=0")
public class Client extends Auditable {
    @Id
    private Long id;
    @Embedded
    private ClientId clientId;
    private String name;
    private String secret;
    private String description;

    @Convert(converter = Authority.AuthorityConverter.class)
    private Set<Authority> authorities = EnumSet.noneOf(Authority.class);

    @Convert(converter = Scope.ScopeConverter.class)
    private Set<Scope> scopes = EnumSet.noneOf(Scope.class);

    @ElementCollection(fetch = FetchType.LAZY)
    @Embedded
    @CollectionTable(
            name = "resources_map",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    private Set<ClientId> resources = new HashSet<>();
    @Column(name = "accessible_")
    private boolean accessible = false;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "client_credentials_gt_enabled")),
            @AttributeOverride(name = "accessTokenValiditySeconds", column = @Column(name = "client_credentials_gt_access_token_validity_seconds"))
    })
    private ClientCredentialsGrant clientCredentialsGrant;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "password_gt_enabled")),
            @AttributeOverride(name = "accessTokenValiditySeconds", column = @Column(name = "password_gt_access_token_validity_seconds"))
    })
    private PasswordGrant passwordGrant;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "authorization_code_gt_enabled")),
            @AttributeOverride(name = "accessTokenValiditySeconds", column = @Column(name = "authorization_code_gt_access_token_validity_seconds"))
    })
    private AuthorizationCodeGrant authorizationCodeGrant;
    private Integer version;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = EnumSet.copyOf(scopes);
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = EnumSet.copyOf(authorities);
        if (accessible) {
            if (
                    authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_BACKEND))
                            || authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_FIRST_PARTY))
            )
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
        if (!resources.isEmpty()) {
            List<Client> clientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(resources));
            boolean b = clientsOfQuery.stream().anyMatch(e -> !e.accessible);
            if (b) {
                throw new IllegalArgumentException("invalid resource(s) found");
            }
            this.resources = new HashSet<>(resources);
        }
    }

    public boolean nonRoot() {
        return this.authorities.stream().noneMatch(Authority.ROLE_ROOT::equals);
    }

    public long id() {
        return id;
    }

    public ClientId clientId() {
        return clientId;
    }

    public String secret() {
        return secret;
    }

    public ClientCredentialsGrant clientCredentialsGrant() {
        return clientCredentialsGrant;
    }

    public PasswordGrant passwordGrant() {
        return passwordGrant;
    }

    private boolean resourcesChanged(Set<ClientId> clientIds) {
        return !ObjectUtils.equals(this.resources, clientIds);
    }

    private boolean accessibleChanged(boolean b) {
        return accessible() != b;
    }

    public boolean accessible() {
        return accessible;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Set<Authority> authorities() {
        return authorities;
    }

    public Set<Scope> scopes() {
        return scopes;
    }

    public Set<ClientId> resources() {
        return resources;
    }

    private boolean authoritiesChanged(Set<Authority> authorities) {
        return !ObjectUtils.equals(this.authorities, authorities);
    }

    private boolean scopesChanged(Set<Scope> scopes) {
        return !ObjectUtils.equals(this.scopes, scopes);
    }

    public Client() {
    }

    public void setId(Long id) {
        this.id = id;
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
                  AuthorizationCodeGrant authorizationCodeGrant,
                  UniqueIdGeneratorService uniqueIdGeneratorService
    ) {
        setId(uniqueIdGeneratorService.id());
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
        if (clientCredentialsGrant != null && clientCredentialsGrant.enabled()) {
            grantTypes.add(clientCredentialsGrant.name());
        }
        if (passwordGrant != null && passwordGrant.enabled()) {
            grantTypes.add(passwordGrant.name());
            if (passwordGrant.refreshTokenGrant() != null && passwordGrant.refreshTokenGrant().enabled()) {
                grantTypes.add(RefreshTokenGrant.NAME);
            }
        }
        if (authorizationCodeGrant != null && authorizationCodeGrant.enabled()) {
            grantTypes.add(authorizationCodeGrant.name());
        }
        return grantTypes;
    }

    public AuthorizationCodeGrant authorizationCodeGrant() {
        return authorizationCodeGrant;
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
        ClientCredentialsGrant.detectChange(this.clientCredentialsGrant(), clientCredentialsGrant, clientId());
        setClientCredentialsGrant(clientCredentialsGrant);
        PasswordGrant.detectChange(this.passwordGrant(), passwordGrant, clientId());
        setPasswordGrant(passwordGrant);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    public Integer version() {
        return version;
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
        setSecret(secret);
        ClientCredentialsGrant.detectChange(this.clientCredentialsGrant(), clientCredentialsGrant, clientId());
        setClientCredentialsGrant(clientCredentialsGrant);
        PasswordGrant.detectChange(this.passwordGrant(), passwordGrant, clientId());
        setPasswordGrant(passwordGrant);
        AuthorizationCodeGrant.detectChange(this.authorizationCodeGrant(), authorizationCodeGrant, clientId());
        setAuthorizationCodeGrant(authorizationCodeGrant);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    private void setSecret(String secret) {
        this.secret = DomainRegistry.encryptionService().encryptedValue(secret);
    }

    private boolean secretChanged(String secret) {
        return StringUtils.hasText(secret);
    }

    public int accessTokenValiditySeconds() {
        if (clientCredentialsGrant() != null) {
            return clientCredentialsGrant().accessTokenValiditySeconds();
        } else if (passwordGrant() != null) {
            return passwordGrant().accessTokenValiditySeconds();
        } else if (authorizationCodeGrant() != null) {
            return authorizationCodeGrant().accessTokenValiditySeconds();
        } else {
            return 0;
        }

    }
}
