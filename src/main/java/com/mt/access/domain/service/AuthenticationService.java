package com.mt.access.domain.service;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.Scope;
import com.mt.access.domain.model.user.Role;
import com.mt.access.domain.model.user.UserId;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface AuthenticationService {
    boolean userInRole(Role role);

    Set<Role> userRoles();

    Set<Scope> clientScopes();

    boolean isClient();

    boolean isUser();

    Authentication getAuthentication();

    UserId getUserId();

    ClientId getClientId();
}
