package com.mt.identityaccess.application.user;

import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.domain.model.user.Role;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class UserRepresentation {
    private Long id;

    private String email;
    private boolean locked;
    private Set<Role> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    private boolean subscription;

    public UserRepresentation(User bizUser) {
        BeanUtils.copyProperties(bizUser, this);
        this.createdAt = bizUser.getCreatedAt().getTime();
        this.modifiedAt = bizUser.getModifiedAt().getTime();
    }
}
