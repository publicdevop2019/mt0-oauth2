package com.hw.infrastructure.service;

import com.hw.application.AuthenticationApplicationService;
import com.hw.domain.model.user.Role;
import com.hw.infrastructure.JwtThreadLocal;
import com.hw.shared.ServiceUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtAuthenticationService implements AuthenticationApplicationService {
    @Override
    public boolean userInRole(Role role) {
        String jwt = JwtThreadLocal.get();
        List<String> authorities = ServiceUtility.getAuthorities(jwt);
        return authorities.stream().anyMatch(e -> role.toString().equals(e));
    }

    @Override
    public boolean isClient() {
        String jwt = JwtThreadLocal.get();
        return ServiceUtility.getUserId(jwt) == null && ServiceUtility.getClientId(jwt) != null;
    }

    @Override
    public boolean isUser() {
        String jwt = JwtThreadLocal.get();
        return ServiceUtility.getUserId(jwt) != null;
    }

    @Override
    public Authentication getAuthentication() {
        String jwt = JwtThreadLocal.get();
        try {
            Collection<? extends GrantedAuthority> au = ServiceUtility.getAuthorities(jwt).stream().map(e -> (GrantedAuthority) () -> e).collect(Collectors.toList());
            String userId = ServiceUtility.getUserId(jwt);
            return new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return au;
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    /**required for authorization code flow*/
                    return userId;
                }

                @Override
                public boolean isAuthenticated() {
                    return false;
                }

                @Override
                public void setAuthenticated(boolean b) {
                    // not used
                }

                @Override
                public String getName() {
                    return null;
                }
            };
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unable to create authentication obj in authorization header");
        }
    }
}