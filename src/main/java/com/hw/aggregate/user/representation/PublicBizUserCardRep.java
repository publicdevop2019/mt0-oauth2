package com.hw.aggregate.user.representation;

import com.hw.aggregate.user.model.BizUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PublicBizUserCardRep {
    private Long id;

    public PublicBizUserCardRep(BizUser bizUser) {
        BeanUtils.copyProperties(bizUser, this);
    }
}
