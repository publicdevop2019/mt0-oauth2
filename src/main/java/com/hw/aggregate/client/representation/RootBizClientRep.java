package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class RootBizClientRep extends RootBizClientCardRep {

    private String clientSecret;

    private Boolean hasSecret;

    public RootBizClientRep(BizClient client) {
        BeanUtils.copyProperties(client, this);
    }
}
