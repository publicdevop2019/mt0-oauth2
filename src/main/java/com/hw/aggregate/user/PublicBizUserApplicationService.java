package com.hw.aggregate.user;

import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.user.command.ForgetPasswordCommand;
import com.hw.aggregate.user.command.PublicCreateBizUserCommand;
import com.hw.aggregate.user.command.PublicResetPwdCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.representation.PublicBizUserCardRep;
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
public class PublicBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, PublicBizUserCardRep, Void, VoidTypedClass> {
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RevokeBizUserTokenService tokenRevocationService;

    @Autowired
    private PwdResetEmailService emailService;
    @Autowired
    private AppPendingUserApplicationService pendingUserApplicationService;
    @Autowired
    private AppBizUserApplicationService appBizUserApplicationService;


    @PostConstruct
    private void setUp() {
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
    }

    @Transactional
    public void sendForgetPassword(ForgetPasswordCommand command) {
        BizUser.createForgetPwdToken(command, this);
    }

    @Transactional
    public void resetPassword(PublicResetPwdCommand command) {
        BizUser.resetPwd(command, this);
    }


    @Override
    public BizUser replaceEntity(BizUser bizUser, Object command) {
        bizUser.replace(command, emailService, tokenRevocationService, encoder);
        return bizUser;
    }

    @Override
    public PublicBizUserCardRep getEntitySumRepresentation(BizUser bizUser) {
        return new PublicBizUserCardRep(bizUser);
    }

    @Override
    public Void getEntityRepresentation(BizUser bizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected BizUser createEntity(long id, Object command) {
        return BizUser.create(id, (PublicCreateBizUserCommand) command, encoder, pendingUserApplicationService, appBizUserApplicationService);

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
