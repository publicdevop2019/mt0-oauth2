package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.Role;
import com.mt.access.domain.model.user.User;
import lombok.Data;

import java.util.Set;

@Data
public class UserCardRepresentation {
    private String id;

    private String email;

    private boolean locked;
    private long createdAt;

    private Set<Role> grantedAuthorities;

    public UserCardRepresentation(Object o) {
        User user = (User) o;
        email = user.getEmail().getEmail();
        id = user.getUserId().getDomainId();
        locked = user.isLocked();
        this.createdAt = user.getCreatedAt().getTime();
        grantedAuthorities = user.getGrantedAuthorities();
    }

}
