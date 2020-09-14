package com.hw.aggregate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserQueryRegistry;
import com.hw.aggregate.user.representation.AppBizUserCardRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@Slf4j
public class AppBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, AppBizUserCardRep, Void, VoidTypedClass> {
    @Autowired
    private BizUserRepo resourceOwnerRepo;

    @Autowired
    private BizUserQueryRegistry bizUserQueryRegistry;

    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private AppChangeRecordApplicationService changeRepository2;
    @Autowired
    private ObjectMapper objectMapper;


    @PostConstruct
    private void setUp() {
        repo = resourceOwnerRepo;
        queryRegistry = bizUserQueryRegistry;
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
        idGenerator = idGenerator2;
        appChangeRecordApplicationService = changeRepository2;
        om = objectMapper;
    }

    @Override
    public BizUser replaceEntity(BizUser bizUser, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AppBizUserCardRep getEntitySumRepresentation(BizUser bizUser) {
        return new AppBizUserCardRep(bizUser);
    }

    @Override
    public Void getEntityRepresentation(BizUser bizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected BizUser createEntity(long id, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void preDelete(BizUser bizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postDelete(BizUser bizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void prePatch(BizUser bizUser, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void postPatch(BizUser bizUser, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }


}
