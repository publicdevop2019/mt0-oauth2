package com.mt.identityaccess.application.client;

import com.mt.common.sql.exception.EmptyQueryValueException;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.user.Role;

import java.util.Set;
import java.util.stream.Collectors;

public class EndpointQuery {
    private String value;

    public String value() {
        return value;
    }

    public EndpointQuery(String queryParam) {
        this.value = queryParam;
        validate();
    }

    public EndpointQuery(Set<ClientId> resources) {
        this.value = "clientId:" + String.join(".", resources.stream().map(ClientId::getDomainId).collect(Collectors.toSet()));
    }

    public void validate() {
        boolean isRoot = DomainRegistry.authenticationService().isUser()
                && DomainRegistry.authenticationService().userInRole(Role.ROLE_ROOT);
        boolean isUser = DomainRegistry.authenticationService().isUser()
                && DomainRegistry.authenticationService().userInRole(Role.ROLE_USER);
        if (isRoot || isUser) {
            if (value == null) {
                if (!isRoot) {
                    throw new IllegalArgumentException("only root role allows empty query");
                }
            } else {
                String[] queryParams = value.split(",");
                for (String param : queryParams) {
                    String[] split = param.split(":");
                    if (split.length != 2) {
                        throw new EmptyQueryValueException();
                    }
                    //only root user and general user can query, general user can only query by id
                    if (!isRoot) {
                        if (!"id".equals(split[0]))
                            throw new IllegalArgumentException("user role can only query by id");
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("only root/user role allows empty query");
        }
    }
}
