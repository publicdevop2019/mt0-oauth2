package com.hw.application.deprecated;

import com.hw.application.command.AppCreatePendingUserCommand;
import com.hw.application.representation.AppPendingUserCardRep;
import com.hw.domain.model.UserNotificationService;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.domain.model.pending_user.PendingUser;
import com.hw.domain.model.pending_user.PendingUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppPendingUserApplicationService extends RoleBasedRestfulService<PendingUser, AppPendingUserCardRep, Void, VoidTypedClass> {
    {
        entityClass = PendingUser.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Autowired
    private UserNotificationService activationCodeEmailService;
    @Autowired
    private PendingUserRepository pendingBizUserRepo;
    @Autowired
    private AppBizUserApplicationService bizUserApplicationService;

    @Override
    public AppPendingUserCardRep getEntitySumRepresentation(PendingUser pendingBizUser) {
        return new AppPendingUserCardRep(pendingBizUser);
    }

    @Override
    protected PendingUser createEntity(long id, Object command) {
        PendingUser pendingBizUser = PendingUser.create(((AppCreatePendingUserCommand) command).getEmail(), pendingBizUserRepo, bizUserApplicationService, idGenerator);
//        activationCodeEmailService.sendActivationCodeTo( pendingBizUser.getEmail(),pendingBizUser.getActivationCode());
        return pendingBizUser;
    }

}
