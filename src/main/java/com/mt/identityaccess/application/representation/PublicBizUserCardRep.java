package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.user.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PublicBizUserCardRep {
    private Long id;

    public PublicBizUserCardRep(User bizUser) {
        BeanUtils.copyProperties(bizUser, this);
    }
}
