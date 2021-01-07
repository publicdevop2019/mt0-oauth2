package com.mt.identityaccess.application.user.command;

import com.mt.identityaccess.domain.model.user.Role;
import com.mt.common.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class UpdateUserCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private boolean locked;

    private Set<Role> grantedAuthorities;

    private boolean subscription;
    private Integer version;
}
