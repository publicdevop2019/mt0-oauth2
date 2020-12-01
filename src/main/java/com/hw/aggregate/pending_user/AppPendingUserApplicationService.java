package com.hw.aggregate.pending_user;

import com.hw.aggregate.pending_user.command.AppCreatePendingUserCommand;
import com.hw.aggregate.pending_user.model.PendingUser;
import com.hw.aggregate.pending_user.representation.AppPendingUserCardRep;
import com.hw.aggregate.user.AppBizUserApplicationService;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppPendingUserApplicationService extends RoleBasedRestfulService<PendingUser, AppPendingUserCardRep, Void, VoidTypedClass> {
    {
        entityClass = PendingUser.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Autowired
    private ActivationCodeEmailService activationCodeEmailService;
    @Autowired
    private PendingUserRepo pendingBizUserRepo;
    @Autowired
    private AppBizUserApplicationService bizUserApplicationService;

    @Override
    public AppPendingUserCardRep getEntitySumRepresentation(PendingUser pendingBizUser) {
        return new AppPendingUserCardRep(pendingBizUser);
    }

    @Override
    protected PendingUser createEntity(long id, Object command) {
        PendingUser pendingBizUser = PendingUser.create(((AppCreatePendingUserCommand) command).getEmail(), pendingBizUserRepo, bizUserApplicationService, idGenerator);
        activationCodeEmailService.sendActivationCode(pendingBizUser.getActivationCode(), pendingBizUser.getEmail());
        return pendingBizUser;
    }

}
