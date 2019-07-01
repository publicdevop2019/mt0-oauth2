package com.hw.clazz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.Valid;

/**
 * rename filed to avoid setter & getter type different
 * @param <T>
 */
public class GrantedAuthorityImpl<T extends Enum<T>> implements GrantedAuthority {
    @Valid
    @Setter
    @Getter
    private T grantedAuthority;

    public GrantedAuthorityImpl(T input) {
        grantedAuthority = input;
    }

    public GrantedAuthorityImpl() {
    }

    public static <T extends Enum> GrantedAuthorityImpl getGrantedAuthority(Class<T> enumType, String string) {
        return new GrantedAuthorityImpl(T.valueOf(enumType, string));
    }
    @JsonIgnore
    public String getAuthority() {
        return grantedAuthority.toString();
    }

}