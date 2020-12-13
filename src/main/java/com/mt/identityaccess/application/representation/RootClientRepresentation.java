package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.client.Client;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class RootClientRepresentation extends RootClientCardRepresentation {

    private String clientSecret;

    private Boolean hasSecret;

    public RootClientRepresentation(Client client) {
        BeanUtils.copyProperties(client, this);
    }
}
