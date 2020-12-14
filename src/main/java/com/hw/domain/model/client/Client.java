package com.hw.domain.model.client;

import com.hw.application.client.ClientQuery;
import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.DomainRegistry;
import com.hw.domain.model.client.event.*;
import com.hw.shared.Auditable;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table
@Entity
@Setter
public class Client extends Auditable {
    @Id
    private long id;
    @Embedded
    private ClientId clientId;
    private String name;
    private String description;
    @Convert(converter = Authority.ClientAuthorityConverter.class)
    private Set<Authority> authorities;
    @Convert(converter = Scope.ScopeSetConverter.class)
    private Set<Scope> scopes;
    @Convert(converter = ClientId.Converter.class)
    private Set<ClientId> resources;
    @Column(name = "_accessible")
    private boolean accessible = false;
    @Embedded
    private ClientCredentialsGrantDetail clientCredentialsGrantDetail;
    @Embedded
    private PasswordGrantDetail passwordGrantDetail;
    @Embedded
    private AuthorizationCodeGrantDetail authorizationCodeGrantDetail;
    @Embedded
    private RefreshTokenGrantDetail refreshTokenGrantDetail;
    @Embedded
    private AccessTokenDetail accessTokenDetail;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
    }

    public void setAuthorities(Set<Authority> authorities) {
        if (accessible) {
            if (
                    authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_BACKEND))
                            || authorities.stream().noneMatch(e -> e.equals(Authority.ROLE_FIRST_PARTY))
            )
                setAccessible(false);
        }
        this.authorities = authorities;
    }

    public void setAccessible(boolean accessible) {
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
        List<Client> clientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(resources));
        boolean b = clientsOfQuery.stream().anyMatch(e -> !e.accessible);
        if (b) {
            throw new IllegalArgumentException("invalid resource(s) found");
        }
        this.resources = resources;
    }

    public boolean nonRoot() {
        return this.authorities.stream().noneMatch(Authority.ROLE_ROOT::equals);
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
                  String description,
                  Set<Scope> scopes,
                  Set<Authority> authorities,
                  Set<ClientId> resources,
                  boolean accessible,
                  ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                  PasswordGrantDetail passwordGrantDetail,
                  RefreshTokenGrantDetail refreshTokenGrantDetail,
                  AuthorizationCodeGrantDetail authorizationCodeGrantDetail,
                  AccessTokenDetail accessTokenDetail
    ) {

        this.setClientId(nextIdentity);
        setResources(resources);
        setScopes(scopes);
        setDescription(description);
        setAccessible(accessible);
        setAuthorities(authorities);
        setName(name);
        this.setClientCredentialsGrantDetail(clientCredentialsGrantDetail);
        this.setPasswordGrantDetail(passwordGrantDetail);
        this.setRefreshTokenGrantDetail(refreshTokenGrantDetail);
        this.setAuthorizationCodeGrantDetail(authorizationCodeGrantDetail);
        this.setAccessTokenDetail(accessTokenDetail);
    }

    public Set<GrantType> totalGrantTypes() {
        HashSet<GrantType> grantTypes = new HashSet<>();
        if (clientCredentialsGrantDetail != null) {
            grantTypes.add(clientCredentialsGrantDetail.getGrantType());
        }
        if (passwordGrantDetail != null) {
            grantTypes.add(passwordGrantDetail.getGrantType());
        }
        if (authorizationCodeGrantDetail != null) {
            grantTypes.add(authorizationCodeGrantDetail.getGrantType());
        }
        if (refreshTokenGrantDetail != null) {
            grantTypes.add(refreshTokenGrantDetail.getGrantType());
        }
        return grantTypes;
    }

    public AccessTokenDetail accessTokenDetail() {
        return accessTokenDetail;
    }

    public AuthorizationCodeGrantDetail authorizationCodeGrantDetail() {
        return authorizationCodeGrantDetail;
    }

    public RefreshTokenGrantDetail refreshTokenGrantDetail() {
        return refreshTokenGrantDetail;
    }

    public ClientId clientId() {
        return clientId;
    }

    public void replace(String name,
                        String description,
                        Set<Scope> scopes,
                        Set<Authority> authorities,
                        Set<ClientId> resources,
                        boolean accessible,
                        ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                        PasswordGrantDetail passwordGrantDetail,
                        AccessTokenDetail accessTokenDetail
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
        this.accessTokenDetail.replace(accessTokenDetail);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void replace(String name,
                        String description,
                        Set<Scope> scopes,
                        Set<Authority> authorities,
                        Set<ClientId> resources,
                        boolean accessible,
                        ClientCredentialsGrantDetail clientCredentialsGrantDetail, PasswordGrantDetail passwordGrantDetail,
                        RefreshTokenGrantDetail refreshTokenGrantDetail, AuthorizationCodeGrantDetail authorizationCodeGrantDetail,
                        AccessTokenDetail accessTokenDetail) {
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
        this.refreshTokenGrantDetail.replace(refreshTokenGrantDetail);
        this.authorizationCodeGrantDetail.replace(authorizationCodeGrantDetail);
        this.accessTokenDetail.replace(accessTokenDetail);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }
}
