package com.hw.clazz;

import org.springframework.security.core.GrantedAuthority;

import javax.validation.Valid;

public class GrantedAuthorityImpl<T extends Enum<T>> implements GrantedAuthority {
    @Valid
    private T authority;

    public GrantedAuthorityImpl(T input) {
        authority = input;
    }

    public GrantedAuthorityImpl() {
    }

    public static <T extends Enum> GrantedAuthorityImpl getGrantedAuthority(Class<T> enumType, String string) {
        return new GrantedAuthorityImpl(T.valueOf(enumType, string));
    }

    @Override
    public String getAuthority() {
        return authority.toString();
    }

    public void setAuthority(T authority) {
        this.authority = authority;
    }
}