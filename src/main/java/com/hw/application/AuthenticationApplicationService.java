package com.hw.application;

import com.hw.domain.model.user.Role;
import org.springframework.security.core.Authentication;

public interface AuthenticationApplicationService {
    boolean userInRole(Role role);

    boolean isClient();

    boolean isUser();

    Authentication getAuthentication();
}
