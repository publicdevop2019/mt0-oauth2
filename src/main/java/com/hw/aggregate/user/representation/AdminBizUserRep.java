package com.hw.aggregate.user.representation;

import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizUserRep {
    private Long id;

    private String email;
    private Boolean locked;
    private Set<BizUserAuthorityEnum> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    private boolean subscription;

    public AdminBizUserRep(BizUser bizUser) {
        this.id = bizUser.getId();
        this.email = bizUser.getEmail();
        this.locked = bizUser.getLocked();
        this.grantedAuthorities = bizUser.getGrantedAuthorities();
        this.createdAt = bizUser.getCreatedAt().getTime();
        this.modifiedAt = bizUser.getModifiedAt().getTime();
        this.createdBy = bizUser.getCreatedBy();
        this.modifiedBy = bizUser.getModifiedBy();
        this.subscription = bizUser.isSubscription();
    }
}
