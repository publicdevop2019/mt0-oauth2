package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.Client;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AppBizClientCardRep {
    private Long id;
    private Boolean resourceIndicator;

    public AppBizClientCardRep(Client client) {
        BeanUtils.copyProperties(client, this);
    }
}
