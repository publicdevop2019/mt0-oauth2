package com.hw.aggregate.user.representation;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import lombok.Data;

import java.util.List;

@Data
public class AdminBizUserCardRep {
    private Long id;

    private String email;

    private Boolean locked;
    private long createAt;

    private List<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities;

    public AdminBizUserCardRep(BizUser bizUser) {
        this.id = bizUser.getId();
        this.email = bizUser.getEmail();
        this.locked = bizUser.getLocked();
        this.grantedAuthorities = bizUser.getGrantedAuthorities();
        this.createAt = bizUser.getCreatedAt().getTime();

    }

}
