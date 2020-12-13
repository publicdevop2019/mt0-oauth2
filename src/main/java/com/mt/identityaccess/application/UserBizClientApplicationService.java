package com.mt.identityaccess.application;

import com.mt.identityaccess.application.representation.UserBizClientCardRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.identityaccess.domain.model.client.BizClient;
import org.springframework.stereotype.Service;

@Service
public class UserBizClientApplicationService extends RoleBasedRestfulService<BizClient, UserBizClientCardRep, Void, VoidTypedClass> {
    {
        entityClass = BizClient.class;
        role = RestfulQueryRegistry.RoleEnum.USER;
    }

    @Override
    public UserBizClientCardRep getEntitySumRepresentation(BizClient client) {
        return new UserBizClientCardRep(client);
    }

}
