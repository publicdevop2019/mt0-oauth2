package com.hw.utility;

import com.hw.shared.ServiceUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

public class ServiceUtilityExt extends ServiceUtility {

    public static Authentication getAuthentication(String bearerHeader) {
        try {
            Collection<? extends GrantedAuthority> au = getAuthority(bearerHeader).stream().map(e -> new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return e;
                }
            }).collect(Collectors.toList());
            String username = getUsername(bearerHeader);
            Authentication authentication = new Authentication() {
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
                    return username;
                }

                @Override
                public boolean isAuthenticated() {
                    return false;
                }

                @Override
                public void setAuthenticated(boolean b) throws IllegalArgumentException {

                }

                @Override
                public String getName() {
                    return null;
                }
            };
            return authentication;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unable to create authentication obj in authorization header");
        }
    }

}
