package com.hw.aggregate.user.command;

import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import com.hw.shared.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AdminUpdateBizUserCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private boolean locked;

    private Set<BizUserAuthorityEnum> grantedAuthorities;

    private boolean subscription;
    private String authorization;
    private Integer version;
}
