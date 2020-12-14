package com.hw.application.client;

import com.hw.application.ApplicationServiceRegistry;
import com.hw.domain.model.client.ClientId;
import com.hw.domain.model.user.Role;
import com.hw.shared.sql.exception.EmptyQueryValueException;

import java.util.Set;
import java.util.stream.Collectors;

public class ClientQuery {
    public String value;

    public ClientQuery(String queryParam) {
        this.value = queryParam;
        validate();
    }

    public ClientQuery(Set<ClientId> resources) {
        this.value = "id:" + String.join(".", resources.stream().map(ClientId::id).collect(Collectors.toSet()));
    }

    public void validate() {
        boolean isRoot = ApplicationServiceRegistry.authenticationApplicationService().isUser()
                && ApplicationServiceRegistry.authenticationApplicationService().userInRole(Role.ROLE_ROOT);
        boolean isUser = ApplicationServiceRegistry.authenticationApplicationService().isUser()
                && ApplicationServiceRegistry.authenticationApplicationService().userInRole(Role.ROLE_USER);
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
                    if (isUser) {
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
