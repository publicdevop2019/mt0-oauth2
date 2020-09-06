package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import lombok.Data;

@Data
public class AppBizClientCardRep {
    private Long id;
    private Boolean resourceIndicator;
    private Boolean autoApprove;

    public AppBizClientCardRep(BizClient client) {
        this.id = client.getId();
        this.resourceIndicator = client.getResourceIndicator();
        this.autoApprove = client.getAutoApprove();
    }
}
