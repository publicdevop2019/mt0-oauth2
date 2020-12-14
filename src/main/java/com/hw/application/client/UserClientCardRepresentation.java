package com.hw.application.client;

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
