package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.BizClient;
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
