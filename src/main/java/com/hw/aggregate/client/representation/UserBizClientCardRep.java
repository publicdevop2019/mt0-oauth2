package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import lombok.Data;

@Data
public class UserBizClientCardRep {

    private String clientId;
    public static final String CUS_REP_CLIENT_ID = "clientId";

    private Boolean autoApprove;

    public UserBizClientCardRep(BizClient client) {
        this.clientId = client.getClientId();
        this.autoApprove = client.getAutoApprove();
    }
}
