package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.BizClient;
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
