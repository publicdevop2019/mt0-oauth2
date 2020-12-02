package com.hw.aggregate.user.representation;

import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizUserCardRep {
    private Long id;

    private String email;

    private boolean locked;
    private long createdAt;

    private Set<BizUserAuthorityEnum> grantedAuthorities;

    public AdminBizUserCardRep(BizUser bizUser) {
        BeanUtils.copyProperties(bizUser, this);
        this.createdAt = bizUser.getCreatedAt().getTime();

    }

}
