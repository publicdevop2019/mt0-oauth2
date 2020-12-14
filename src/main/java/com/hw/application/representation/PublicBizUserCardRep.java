package com.hw.application.representation;

import com.hw.domain.model.user.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PublicBizUserCardRep {
    private Long id;

    public PublicBizUserCardRep(User bizUser) {
        BeanUtils.copyProperties(bizUser, this);
    }
}
