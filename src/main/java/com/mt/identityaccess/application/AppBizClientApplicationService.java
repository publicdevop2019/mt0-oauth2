package com.mt.identityaccess.application;

import com.mt.identityaccess.application.representation.AppBizClientCardRep;
import com.mt.identityaccess.application.representation.ClientDetailsRepresentation;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AppBizClientApplicationService extends RoleBasedRestfulService<Client, AppBizClientCardRep, ClientDetailsRepresentation, VoidTypedClass> implements ClientDetailsService {
    {
        entityClass = Client.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public AppBizClientCardRep getEntitySumRepresentation(Client client) {
        return new AppBizClientCardRep(client);
    }

    @Override
    public ClientDetailsRepresentation getEntityRepresentation(Client client) {
        return new ClientDetailsRepresentation(client);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return readById(Long.parseLong(clientId));
    }
}
