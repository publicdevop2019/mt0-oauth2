package com.hw.aggregate.user.representation;

import com.hw.aggregate.user.model.BizUser;
import lombok.Data;

@Data
public class AppBizUserCardRep {
    private Long id;

    private String email;

    public AppBizUserCardRep(BizUser bizUser) {
        this.id = bizUser.getId();
        this.email = bizUser.getEmail();
    }
}
