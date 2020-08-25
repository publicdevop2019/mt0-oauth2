package com.hw.aggregate.client;

import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.model.BizClientQueryRegistry;
import com.hw.aggregate.client.representation.UserBizClientCardRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserBizClientApplicationService extends DefaultRoleBasedRestfulService<BizClient, UserBizClientCardRep, Void, VoidTypedClass> {
    @Autowired
    private BizClientRepo clientRepo;
    @Autowired
    private BizClientQueryRegistry clientQueryRegistry;

    @PostConstruct
    private void setUp() {
        repo = clientRepo;
        queryRegistry = clientQueryRegistry;
        entityClass = BizClient.class;
        role = RestfulQueryRegistry.RoleEnum.USER;
    }

    @Override
    public BizClient replaceEntity(BizClient stored, Object command) {
        return null;
    }

    @Override
    public UserBizClientCardRep getEntitySumRepresentation(BizClient client) {
        return new UserBizClientCardRep(client);
    }

    @Override
    public Void getEntityRepresentation(BizClient client) {
        return null;
    }

    @Override
    protected BizClient createEntity(long id, Object command) {
        return null;
    }
}
