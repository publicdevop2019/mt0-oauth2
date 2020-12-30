package com.mt.identityaccess.application.user;

import com.mt.identityaccess.domain.model.user.Role;
import com.mt.identityaccess.domain.model.user.User;
import lombok.Data;

import java.util.Set;

@Data
public class UserRepresentation {
    private String id;

    private String email;
    private boolean locked;
    private Set<Role> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    private boolean subscription;

    public UserRepresentation(User user) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        this.locked = user.isLocked();
        this.grantedAuthorities = user.getGrantedAuthorities();
        this.createdBy = user.getCreatedBy();
        this.createdAt = user.getCreatedAt().getTime();
        this.modifiedBy = user.getModifiedBy();
        this.modifiedAt = user.getModifiedAt().getTime();
        this.subscription = user.isSubscription();
    }
}
