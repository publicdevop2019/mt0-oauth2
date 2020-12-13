package com.mt.identityaccess.domain.model.client;

import com.mt.identityaccess.domain.model.DomainRegistry;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class BasicClientDetail {
    private String name;
    private String secret;
    private String description;

    private Set<Authority> authorities;
    private Set<Scope> scopes;
    private Set<ClientId> resources;
    private boolean accessible = false;

    public BasicClientDetail(String name, String secret, String description, Set<Scope> scopes, Set<Authority> authorities, Set<ClientId> resources, boolean accessible) {
        this(name, description, scopes, authorities, resources, accessible);
        this.setSecret(secret);
    }

    public BasicClientDetail(String name, String description, Set<Scope> scopes, Set<Authority> authorities, Set<ClientId> resources, boolean accessible) {
        this.setName(name);
        this.setDescription(description);
        this.setScopes(scopes);
        this.setAuthorities(authorities);
        this.setResources(resources);
        this.setAccessible(accessible);
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
        List<Client> clientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQueryParam(resources));
        boolean b = clientsOfQuery.stream().anyMatch(e -> !e.basicClientDetail().accessible);
        if (b) {
            throw new IllegalArgumentException("invalid resource(s) found");
        }
        this.resources = resources;
    }

    public boolean nonRoot() {
        return this.authorities.stream().noneMatch(Authority.ROLE_ROOT::equals);
    }

    public void setSecret(String secret) {
        this.secret = DomainRegistry.encryptionService().encryptedValue(secret);
    }
}
