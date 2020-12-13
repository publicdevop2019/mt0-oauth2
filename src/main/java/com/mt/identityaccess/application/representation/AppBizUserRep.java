package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.app.GrantedAuthorityImpl;
import com.mt.identityaccess.domain.model.user.BizUser;
import com.mt.identityaccess.domain.model.user.BizUserAuthorityEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class AppBizUserRep implements UserDetails {
    private Long id;
    private String password;
    private boolean locked;
    private Set<BizUserAuthorityEnum> grantedAuthorities;

    public AppBizUserRep(BizUser bizUser) {
        BeanUtils.copyProperties(bizUser, this);
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
}
