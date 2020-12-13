package com.mt.identityaccess.application;

import com.mt.identityaccess.application.representation.AppBizClientCardRep;
import com.mt.identityaccess.application.representation.AppBizClientRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.identityaccess.domain.model.client.BizClient;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AppBizClientApplicationService extends RoleBasedRestfulService<BizClient, AppBizClientCardRep, AppBizClientRep, VoidTypedClass> implements ClientDetailsService {
    {
        entityClass = BizClient.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public AppBizClientCardRep getEntitySumRepresentation(BizClient client) {
        return new AppBizClientCardRep(client);
    }

    @Override
    public AppBizClientRep getEntityRepresentation(BizClient client) {
        return new AppBizClientRep(client);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return readById(Long.parseLong(clientId));
    }
}
