package com.mt.access.application.user.command;

import com.mt.access.domain.model.user.Role;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class UpdateUserCommand implements Serializable{
    private static final long serialVersionUID = 1;
    private boolean locked;

    private Set<Role> grantedAuthorities;

    private boolean subscription;
    private Integer version;
}
