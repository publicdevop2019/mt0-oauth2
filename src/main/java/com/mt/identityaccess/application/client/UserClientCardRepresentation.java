package com.mt.identityaccess.application.client;

import com.mt.identityaccess.domain.model.client.Client;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserClientCardRepresentation {
    private Long id;
    private Boolean autoApprove;

    public UserClientCardRepresentation(Object client) {
        BeanUtils.copyProperties(client, this);
    }
}
