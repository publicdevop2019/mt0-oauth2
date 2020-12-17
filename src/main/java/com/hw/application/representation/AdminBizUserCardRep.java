package com.hw.application.representation;

import com.hw.domain.model.user.User;
import com.hw.domain.model.user.Role;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizUserCardRep {
    private Long id;

    private String email;

    private boolean locked;
    private long createdAt;

    private Set<Role> grantedAuthorities;

    public AdminBizUserCardRep(User bizUser) {
        BeanUtils.copyProperties(bizUser, this);
        this.createdAt = bizUser.getCreatedAt().getTime();

    }

}