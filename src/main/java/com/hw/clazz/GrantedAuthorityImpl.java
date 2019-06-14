package com.hw.clazz;

import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NotNull
public class GrantedAuthorityImpl<T extends Enum> implements GrantedAuthority {

    @NotBlank
    private T authority;

    public GrantedAuthorityImpl(T input) {
        authority = input;
    }

    public GrantedAuthorityImpl() {
    }

    public static <T extends Enum> GrantedAuthorityImpl<T> getGrantedAuthority(Class<T> enumType,String string) {
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