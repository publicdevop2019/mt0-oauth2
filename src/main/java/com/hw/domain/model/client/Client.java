package com.hw.domain.model.client;

import com.hw.application.client.ClientQuery;
import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.DomainRegistry;
import com.hw.domain.model.client.event.*;
import com.hw.shared.Auditable;
import com.hw.shared.IdGenerator;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table
@Entity
@Setter
public class Client extends Auditable {
    @Id
    private Long id;
    @Embedded
    private ClientId clientId;
    private String name;
    private String secret;
    private String description;
    //    @Convert(converter = Authority.ClientAuthorityConverter.class)
    private HashSet<Authority> authorities = new HashSet<>();
    //    @Convert(converter = Scope.ScopeSetConverter.class)
    private HashSet<Scope> scopes = new HashSet<>();
    //    @Convert(converter = ClientId.Converter.class)
    private HashSet<ClientId> resources = new HashSet<>();
    @Column(name = "_accessible")
    private boolean accessible = false;
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientCredentialsGrantDetail clientCredentialsGrantDetail;
    @Transient
//    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordGrantDetail passwordGrantDetail;
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AuthorizationCodeGrantDetail authorizationCodeGrantDetail;
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

    public ClientCredentialsGrantDetail clientCredentialsGrantDetail() {
        return clientCredentialsGrantDetail;
    }

    public PasswordGrantDetail passwordGrantDetail() {
        return passwordGrantDetail;
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
                  ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                  PasswordGrantDetail passwordGrantDetail,
                  AuthorizationCodeGrantDetail authorizationCodeGrantDetail
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
        setClientCredentialsGrantDetail(clientCredentialsGrantDetail);
        setPasswordGrantDetail(passwordGrantDetail);
        setAuthorizationCodeGrantDetail(authorizationCodeGrantDetail);
        clientCredentialsGrantDetail.internalOnlySetClient(this);
        passwordGrantDetail.internalOnlySetClient(this);
        authorizationCodeGrantDetail.internalOnlySetClient(this);
    }

    public Set<GrantType> totalGrantTypes() {
        HashSet<GrantType> grantTypes = new HashSet<>();
        if (clientCredentialsGrantDetail != null && clientCredentialsGrantDetail.enabled()) {
            grantTypes.add(clientCredentialsGrantDetail.name());
        }
        if (passwordGrantDetail != null && passwordGrantDetail.enabled()) {
            grantTypes.add(passwordGrantDetail.name());
            if (passwordGrantDetail.refreshTokenGrantDetail() != null && passwordGrantDetail.refreshTokenGrantDetail().enabled()) {
                grantTypes.add(RefreshTokenGrantDetail.NAME);
            }
        }
        if (authorizationCodeGrantDetail != null && authorizationCodeGrantDetail.enabled()) {
            grantTypes.add(authorizationCodeGrantDetail.name());
        }
        return grantTypes;
    }

    public AuthorizationCodeGrantDetail authorizationCodeGrantDetail() {
        return authorizationCodeGrantDetail;
    }

    public void replace(String name,
                        String description,
                        boolean accessible,
                        Set<Scope> scopes,
                        Set<Authority> authorities,
                        Set<ClientId> resources,
                        ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                        PasswordGrantDetail passwordGrantDetail
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
        this.clientCredentialsGrantDetail.replace(clientCredentialsGrantDetail);
        this.passwordGrantDetail.replace(passwordGrantDetail);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    public Integer getVersion() {
        return version;
    }

    public void replace(String name,
                        String secret,
                        String description,
                        boolean accessible,
                        Set<Scope> scopes,
                        Set<Authority> authorities,
                        Set<ClientId> resources,
                        ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                        PasswordGrantDetail passwordGrantDetail,
                        AuthorizationCodeGrantDetail authorizationCodeGrantDetail
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
        this.clientCredentialsGrantDetail.replace(clientCredentialsGrantDetail);
        this.passwordGrantDetail.replace(passwordGrantDetail);
        this.authorizationCodeGrantDetail.replace(authorizationCodeGrantDetail);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    private void setSecret(String secret) {
        this.secret = DomainRegistry.encryptionService().encryptedValue(secret);
    }

    private boolean secretChanged(String secret) {
        return StringUtils.hasText(secret);
    }

    public int accessTokenValiditySeconds() {
        if (clientCredentialsGrantDetail() != null) {
            return clientCredentialsGrantDetail().accessTokenValiditySeconds();
        } else if (passwordGrantDetail() != null) {
            return passwordGrantDetail().accessTokenValiditySeconds();
        } else if (authorizationCodeGrantDetail() != null) {
            return authorizationCodeGrantDetail().accessTokenValiditySeconds();
        } else {
            return 0;
        }

    }
}
