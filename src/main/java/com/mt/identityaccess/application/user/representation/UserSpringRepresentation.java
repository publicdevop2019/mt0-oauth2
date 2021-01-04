package com.mt.identityaccess.application.user.representation;

import com.mt.identityaccess.domain.model.user.Role;
import com.mt.identityaccess.domain.model.user.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserSpringRepresentation implements UserDetails {
    private String id;
    private String password;
    private boolean locked;
    private Set<Role> grantedAuthorities;

    public UserSpringRepresentation(User user) {
        id = user.getUserId().getDomainId();
        password = user.getPassword().getPassword();
        locked = user.isLocked();
        grantedAuthorities = user.getGrantedAuthorities();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class GrantedAuthorityImpl<T extends Enum<T>> implements GrantedAuthority {
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

        public String getAuthority() {
            return grantedAuthority.toString();
        }

    }
}
