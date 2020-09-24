package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import lombok.Data;

@Data
public class UserBizClientCardRep {
    private Long id;
    private Boolean autoApprove;

    public UserBizClientCardRep(BizClient client) {
        this.id = client.getId();
        this.autoApprove = client.getAutoApprove();
    }
}
