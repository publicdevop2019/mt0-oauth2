package com.mt.identityaccess.domain.service;

import com.mt.identityaccess.domain.model.user.Role;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    boolean userInRole(Role role);

    boolean isClient();

    boolean isUser();

    Authentication getAuthentication();
}
