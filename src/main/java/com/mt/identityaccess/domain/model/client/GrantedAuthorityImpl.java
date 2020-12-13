package com.mt.identityaccess.domain.model.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.Valid;
import java.util.Objects;

/**
 * rename filed to avoid setter & getter type different
 *
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

    public static <T extends Enum<T>> GrantedAuthorityImpl<T> getGrantedAuthority(Class<T> enumType, String string) {
        return new GrantedAuthorityImpl<>(T.valueOf(enumType, string));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GrantedAuthorityImpl<?> that = (GrantedAuthorityImpl<?>) o;
        return grantedAuthority.equals(that.grantedAuthority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grantedAuthority);
    }

    @JsonIgnore
    public String getAuthority() {
        return grantedAuthority.toString();
    }

}