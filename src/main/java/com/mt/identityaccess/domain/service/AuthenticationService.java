package com.mt.identityaccess.domain.service;

import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.user.Role;
import com.mt.identityaccess.domain.model.user.UserId;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    boolean userInRole(Role role);

    boolean isClient();

    boolean isUser();

    Authentication getAuthentication();

    UserId getUserId();

    ClientId getClientId();
}
