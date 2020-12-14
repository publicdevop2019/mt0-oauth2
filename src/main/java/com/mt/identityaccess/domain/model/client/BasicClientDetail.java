package com.mt.identityaccess.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.event.ClientAccessibleChanged;
import com.mt.identityaccess.domain.model.client.event.ClientAuthoritiesChanged;
import com.mt.identityaccess.domain.model.client.event.ClientResourcesChanged;
import com.mt.identityaccess.domain.model.client.event.ClientScopesChanged;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class BasicClientDetail {
    private ClientId clientId;
    private String name;
    private String description;
    private Set<Authority> authorities;
    private Set<Scope> scopes;
    private Set<ClientId> resources;
    private boolean accessible = false;

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
        List<Client> clientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(resources));
        boolean b = clientsOfQuery.stream().anyMatch(e -> !e.basicClientDetail().accessible);
        if (b) {
            throw new IllegalArgumentException("invalid resource(s) found");
        }
        this.resources = resources;
    }

    public boolean nonRoot() {
        return this.authorities.stream().noneMatch(Authority.ROLE_ROOT::equals);
    }

    public void replace(BasicClientDetail basicClientDetail) {
        if (authoritiesChanged(basicClientDetail)) {
            DomainEventPublisher.instance().publish(new ClientAuthoritiesChanged(clientId));
        }
        if (scopesChanged(basicClientDetail)) {
            DomainEventPublisher.instance().publish(new ClientScopesChanged(clientId));
        }
        if (resourcesChanged(basicClientDetail)) {
            DomainEventPublisher.instance().publish(new ClientResourcesChanged(clientId));
        }
        if (accessibleChanged(basicClientDetail)) {
            DomainEventPublisher.instance().publish(new ClientAccessibleChanged(clientId));
        }
        setResources(basicClientDetail.resources);
        setScopes(basicClientDetail.scopes);
        setDescription(basicClientDetail.description);
        setAccessible(basicClientDetail.accessible);
        setAuthorities(basicClientDetail.authorities);
        setName(basicClientDetail.name);
    }

    private boolean resourcesChanged(BasicClientDetail basicClientDetail) {
        return !ObjectUtils.equals(this.resources, basicClientDetail.resources);
    }

    private boolean accessibleChanged(BasicClientDetail basicClientDetail) {
        return isAccessible() != basicClientDetail.isAccessible();
    }

    private boolean authoritiesChanged(BasicClientDetail basicClientDetail) {
        return !ObjectUtils.equals(this.authorities, basicClientDetail.authorities);
    }

    private boolean scopesChanged(BasicClientDetail basicClientDetail) {
        return !ObjectUtils.equals(this.scopes, basicClientDetail.scopes);
    }
}
