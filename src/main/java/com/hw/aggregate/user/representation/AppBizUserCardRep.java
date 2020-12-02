package com.hw.aggregate.user.representation;

import com.hw.aggregate.user.model.BizUser;
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
