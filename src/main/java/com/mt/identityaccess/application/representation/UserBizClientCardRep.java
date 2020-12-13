package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.Client;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserBizClientCardRep {
    private Long id;
    private Boolean autoApprove;

    public UserBizClientCardRep(Object client) {
        BeanUtils.copyProperties(client, this);
    }
}
