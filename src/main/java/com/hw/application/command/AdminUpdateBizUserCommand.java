package com.hw.application.command;

import com.hw.domain.model.user.Role;
import com.hw.shared.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AdminUpdateBizUserCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private boolean locked;

    private Set<Role> grantedAuthorities;

    private boolean subscription;
    private String authorization;
    private Integer version;
}