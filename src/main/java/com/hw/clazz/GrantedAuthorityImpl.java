package com.hw.clazz;

import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NotNull
public class GrantedAuthorityImpl implements GrantedAuthority {

    @NotBlank
    private String authority;

    public GrantedAuthorityImpl(String input) {
        authority = input;
    }

    public GrantedAuthorityImpl() {
    }

    public static GrantedAuthority getGrantedAuthority(String string) {

        return new GrantedAuthorityImpl(string);

    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}