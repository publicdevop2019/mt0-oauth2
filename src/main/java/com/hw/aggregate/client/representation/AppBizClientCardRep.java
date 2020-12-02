package com.hw.aggregate.client.representation;

import com.hw.aggregate.client.model.BizClient;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AppBizClientCardRep {
    private Long id;
    private Boolean resourceIndicator;

    public AppBizClientCardRep(BizClient client) {
        BeanUtils.copyProperties(client, this);
    }
}
