package com.hw.aggregate.pending_user;

import com.hw.aggregate.pending_user.command.AppCreatePendingUserCommand;
import com.hw.aggregate.pending_user.model.PendingUser;
import com.hw.aggregate.pending_user.representation.AppPendingUserCardRep;
import com.hw.aggregate.user.AppBizUserApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AppPendingUserApplicationService extends DefaultRoleBasedRestfulService<PendingUser, AppPendingUserCardRep, Void, VoidTypedClass> {
    @Autowired
    private ActivationCodeEmailService activationCodeEmailService;
    @Autowired
    private PendingUserRepo pendingBizUserRepo;
    @Autowired
    private AppBizUserApplicationService bizUserApplicationService;

    @PostConstruct
    private void setUp() {
        entityClass = PendingUser.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public PendingUser replaceEntity(PendingUser pendingBizUser, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AppPendingUserCardRep getEntitySumRepresentation(PendingUser pendingBizUser) {
        return new AppPendingUserCardRep(pendingBizUser);
    }

    @Override
    public Void getEntityRepresentation(PendingUser pendingBizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PendingUser createEntity(long id, Object command) {
        PendingUser pendingBizUser = PendingUser.create(((AppCreatePendingUserCommand) command).getEmail(), pendingBizUserRepo, bizUserApplicationService, idGenerator);
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
