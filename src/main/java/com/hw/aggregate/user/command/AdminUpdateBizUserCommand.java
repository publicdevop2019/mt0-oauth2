package com.hw.aggregate.user.command;

import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AdminUpdateBizUserCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private Boolean locked;

    private Set<BizUserAuthorityEnum> grantedAuthorities;

    private boolean subscription;
    private String authorization;
}
