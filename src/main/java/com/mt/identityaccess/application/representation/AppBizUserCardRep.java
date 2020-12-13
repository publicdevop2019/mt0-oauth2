package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.user.BizUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AppBizUserCardRep {
    private Long id;

    private String email;

    public AppBizUserCardRep(BizUser bizUser) {
        BeanUtils.copyProperties(bizUser, this);
    }
}
