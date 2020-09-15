package com.hw.aggregate.user.representation;

import com.hw.aggregate.user.model.BizUser;
import lombok.Data;

@Data
public class PublicBizUserCardRep {
    private Long id;

    public PublicBizUserCardRep(BizUser bizUser) {
        this.id = bizUser.getId();
    }
}
