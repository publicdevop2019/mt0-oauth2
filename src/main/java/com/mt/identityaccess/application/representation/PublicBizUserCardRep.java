package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.user.BizUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PublicBizUserCardRep {
    private Long id;

    public PublicBizUserCardRep(BizUser bizUser) {
        BeanUtils.copyProperties(bizUser, this);
    }
}
