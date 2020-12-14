package com.mt.identityaccess.port.adapter.persistence;

import com.hw.shared.sql.exception.EmptyQueryValueException;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.user.Role;

import java.util.Set;

public class ClientQuery {
    public String value;

    public ClientQuery(String queryParam) {
        this.value = queryParam;
        validate();
    }

    public ClientQuery(Set<ClientId> clientIdSet) {
        this.value = "id:" + String.join("." + clientIdSet.stream().map(ClientId::id));
    }

    public void validate() {
        String[] queryParams = value.split(",");
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length != 2) {
                throw new EmptyQueryValueException();
            }
            //only root user and general user can query, general user can only query by id
            if (DomainRegistry.authenticationService().isUser() && DomainRegistry.authenticationService().userInRole(Role.ROLE_ROOT)) {

            } else if (DomainRegistry.authenticationService().isUser() && DomainRegistry.authenticationService().userInRole(Role.ROLE_USER)) {
                if (!"id".equals(split[0]))
                    throw new IllegalArgumentException("user role can only query by id");
            } else {
                throw new IllegalArgumentException("only root and user can query");
            }
        }
    }
}
