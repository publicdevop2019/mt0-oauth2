package com.hw.aggregate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.user.command.PublicCreateBizUserCommand;
import com.hw.aggregate.user.command.PublicForgetPasswordCommand;
import com.hw.aggregate.user.command.PublicResetPwdCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserQueryRegistry;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@Slf4j
public class PublicBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, Void, Void, VoidTypedClass> {
    @Autowired
    private BizUserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RevokeBizUserTokenService tokenRevocationService;

    @Autowired
    private PwdResetEmailService emailService;
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
        repo = userRepo;
        queryRegistry = bizUserQueryRegistry;
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
        idGenerator = idGenerator2;
        appChangeRecordApplicationService = changeRepository2;
        om = objectMapper;
    }
    @Transactional
    public void sendForgetPassword(PublicForgetPasswordCommand command) {
        BizUser.createForgetPwdToken(command, userRepo, emailService);
    }

    @Transactional
    public void resetPassword(PublicResetPwdCommand command) {
        BizUser.resetPwd(command, userRepo, tokenRevocationService, encoder);
    }


    @Override
    public BizUser replaceEntity(BizUser bizUser, Object command) {
        throw new UnsupportedOperationException();
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
        return BizUser.create(id, (PublicCreateBizUserCommand) command, encoder, pendingUserApplicationService, bizUserApplicationService);

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
