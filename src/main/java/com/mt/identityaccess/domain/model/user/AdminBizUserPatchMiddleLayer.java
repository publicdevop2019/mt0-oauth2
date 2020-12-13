package com.mt.identityaccess.domain.model.user;

import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizUserPatchMiddleLayer extends TypedClass<AdminBizUserPatchMiddleLayer> {
    private boolean locked;
    private Set<BizUserAuthorityEnum> grantedAuthorities;

    public AdminBizUserPatchMiddleLayer(BizUser bizUser) {
        super(AdminBizUserPatchMiddleLayer.class);
        this.locked = bizUser.isLocked();
        this.grantedAuthorities = bizUser.getGrantedAuthorities();
    }

    public AdminBizUserPatchMiddleLayer() {
        super(AdminBizUserPatchMiddleLayer.class);
    }
}
