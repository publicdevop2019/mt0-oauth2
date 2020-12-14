package com.hw.application.representation;

import com.hw.domain.model.user.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AppBizUserCardRep {
    private Long id;

    private String email;

    public AppBizUserCardRep(User bizUser) {
        BeanUtils.copyProperties(bizUser, this);
    }
}
