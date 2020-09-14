package com.hw.aggregate.pending_user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.pending_user.command.CreatePendingUserCommand;
import com.hw.aggregate.pending_user.model.PendingUser;
import com.hw.aggregate.pending_user.model.PendingUserQueryRegistry;
import com.hw.aggregate.user.AppBizUserApplicationService;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class PublicPendingUserApplicationService extends DefaultRoleBasedRestfulService<PendingUser, Void, Void, VoidTypedClass> {
    @Autowired
    private ActivationCodeEmailService activationCodeEmailService;
    @Autowired
    private PendingUserRepo pendingBizUserRepo;
    @Autowired
    private AppBizUserApplicationService bizUserApplicationService;
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private AppChangeRecordApplicationService changeRepository2;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PendingUserQueryRegistry pendingUserQueryRegistry;

    @PostConstruct
    private void setUp() {
        repo = pendingBizUserRepo;
        queryRegistry = pendingUserQueryRegistry;
        entityClass = PendingUser.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
        idGenerator = idGenerator2;
        appChangeRecordApplicationService = changeRepository2;
        om = objectMapper;
    }

    @Override
    public PendingUser replaceEntity(PendingUser pendingBizUser, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void getEntitySumRepresentation(PendingUser pendingBizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void getEntityRepresentation(PendingUser pendingBizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PendingUser createEntity(long id, Object command) {
        PendingUser pendingBizUser = PendingUser.create(((CreatePendingUserCommand) command).getEmail(), pendingBizUserRepo, bizUserApplicationService, idGenerator);
        activationCodeEmailService.sendActivationCode(pendingBizUser.getActivationCode(), pendingBizUser.getEmail());
        return pendingBizUser;
    }

    @Override
    public void preDelete(PendingUser pendingUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postDelete(PendingUser pendingUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void prePatch(PendingUser pendingUser, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void postPatch(PendingUser pendingUser, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }


}
