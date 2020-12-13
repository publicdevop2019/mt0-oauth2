package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.BizClient;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserBizClientCardRep {
    private Long id;
    private Boolean autoApprove;

    public UserBizClientCardRep(BizClient client) {
        BeanUtils.copyProperties(client, this);
    }
}
