package com.hw.aggregate.user.command;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import lombok.Data;

import java.util.List;

@Data
public class AdminUpdateBizUserCommand {

    private Boolean locked;

    private List<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities;

    private boolean subscription;
    private String authorization;
}
