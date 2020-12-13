package com.mt.identityaccess.infrastructure.service;

import com.hw.shared.ServiceUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

public class UtilityService {
    private UtilityService() {
    }

    public static Authentication getAuthentication(String bearerHeader) {
        try {
            Collection<? extends GrantedAuthority> au = ServiceUtility.getAuthority(bearerHeader).stream().map(e -> (GrantedAuthority) () -> e).collect(Collectors.toList());
            String userId = ServiceUtility.getUserId(bearerHeader);
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
