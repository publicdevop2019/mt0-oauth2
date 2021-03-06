package com.mt.access.infrastructure;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.Scope;
import com.mt.access.domain.model.user.Role;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.service.AuthenticationService;
import com.mt.common.domain.model.jwt.JwtUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtAuthenticationService implements AuthenticationService {
    @Override
    public boolean userInRole(Role role) {
        String jwt = JwtThreadLocal.get();
        List<String> authorities = JwtUtility.getAuthorities(jwt);
        return authorities.stream().anyMatch(e -> role.toString().equals(e));
    }

    @Override
    public Set<Role> userRoles() {
        String jwt = JwtThreadLocal.get();
        List<String> authorities = JwtUtility.getAuthorities(jwt);
        return authorities.stream().map(Role::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Set<Scope> clientScopes() {
        String jwt = JwtThreadLocal.get();
        List<String> authorities = JwtUtility.getScopes(jwt);
        return authorities.stream().map(Scope::valueOf).collect(Collectors.toSet());
    }

    @Override
    public boolean isClient() {
        String jwt = JwtThreadLocal.get();
        return JwtUtility.getUserId(jwt) == null && JwtUtility.getClientId(jwt) != null;
    }

    @Override
    public boolean isUser() {
        String jwt = JwtThreadLocal.get();
        return JwtUtility.getUserId(jwt) != null;
    }

    @Override
    public Authentication getAuthentication() {
        String jwt = JwtThreadLocal.get();
        try {
            Collection<? extends GrantedAuthority> au = JwtUtility.getAuthorities(jwt).stream().map(e -> (GrantedAuthority) () -> e).collect(Collectors.toList());
            String userId = JwtUtility.getUserId(jwt);
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

    @Override
    public UserId getUserId() {
        String jwt = JwtThreadLocal.get();
        return new UserId(JwtUtility.getUserId(jwt));
    }

    @Override
    public ClientId getClientId() {
        String jwt = JwtThreadLocal.get();
        return new ClientId(JwtUtility.getClientId(jwt));
    }

    public static class JwtThreadLocal {
        public static final ThreadLocal<String> jwtThreadLocal = new ThreadLocal<>();

        public static void set(String user) {
            jwtThreadLocal.set(user);
        }

        public static void unset() {
            jwtThreadLocal.remove();
        }

        public static String get() {
            return jwtThreadLocal.get();
        }
    }
}
