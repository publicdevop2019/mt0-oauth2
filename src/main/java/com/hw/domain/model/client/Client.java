package com.hw.domain.model.client;

import com.hw.application.client.ClientQuery;
import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.DomainRegistry;
import com.hw.domain.model.client.event.*;
import com.hw.shared.Auditable;
import com.hw.shared.IdGenerator;
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
@Where(clause="deleted=0")
public class Client extends Auditable {
    @Id
    private Long id;
    @Embedded
    private ClientId clientId;
    private String name;
    private String secret;
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "authorities_map",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    private Set<Authority> authorities = EnumSet.noneOf(Authority.class);

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "scopes_map",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    @Enumerated(EnumType.STRING)
    private Set<Scope> scopes = EnumSet.noneOf(Scope.class);

    @ElementCollection(fetch = FetchType.LAZY)
    @Embedded
    @CollectionTable(
            name = "resources_map",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    private Set<ClientId> resources = new HashSet<>();
    @Column(name = "_accessible")
    private boolean accessible = false;
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientCredentialsGrant clientCredentialsGrant;
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordGrant passwordGrant;
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AuthorizationCodeGrant authorizationCodeGrant;
    private Integer version;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = new HashSet<>(scopes);
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = new HashSet<>(authorities);
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
        this.id = IdGenerator.instance().id();
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
        clientCredentialsGrant.internalOnlySetClient(this);
        passwordGrant.internalOnlySetClient(this);
        authorizationCodeGrant.internalOnlySetClient(this);
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
        this.clientCredentialsGrant.replace(clientCredentialsGrant);
        this.passwordGrant.replace(passwordGrant);
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
        this.clientCredentialsGrant.replace(clientCredentialsGrant);
        this.passwordGrant.replace(passwordGrant);
        this.authorizationCodeGrant.replace(authorizationCodeGrant);
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
