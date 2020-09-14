package com.hw.aggregate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.pending_user.PendingUserRepo;
import com.hw.aggregate.user.command.UserUpdateBizUserCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserQueryRegistry;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class UserBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, Void, Void, VoidTypedClass> {
    @Autowired
    BizUserRepo resourceOwnerRepo;

    @Autowired
    PendingUserRepo pendingResourceOwnerRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    RevokeBizUserTokenService tokenRevocationService;

    @Autowired
    PwdResetEmailService emailService;
    @Autowired
    private BizUserQueryRegistry bizUserQueryRegistry;
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private AppChangeRecordApplicationService changeRepository2;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AppPendingUserApplicationService pendingUserApplicationService;
    @Autowired
    private AppBizUserApplicationService bizUserApplicationService;


    @PostConstruct
    private void setUp() {
        repo = resourceOwnerRepo;
        queryRegistry = bizUserQueryRegistry;
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.USER;
        idGenerator = idGenerator2;
        appChangeRecordApplicationService = changeRepository2;
        om = objectMapper;
    }

    @Override
    public BizUser replaceEntity(BizUser storedBizUser, Object command) {
        return storedBizUser.replace((UserUpdateBizUserCommand) command, tokenRevocationService, encoder);
    }

    @Override
    public Void getEntitySumRepresentation(BizUser bizUser) {
        throw new UnsupportedOperationException();
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
