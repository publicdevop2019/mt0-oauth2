package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.user.BizUser;
import com.mt.identityaccess.domain.model.user.BizUserAuthorityEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizUserRep {
    private Long id;

    private String email;
    private boolean locked;
    private Set<BizUserAuthorityEnum> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    private boolean subscription;

    public AdminBizUserRep(BizUser bizUser) {
        BeanUtils.copyProperties(bizUser, this);
        this.createdAt = bizUser.getCreatedAt().getTime();
        this.modifiedAt = bizUser.getModifiedAt().getTime();
    }
}