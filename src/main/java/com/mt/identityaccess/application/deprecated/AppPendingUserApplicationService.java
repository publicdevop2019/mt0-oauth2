package com.mt.identityaccess.application.deprecated;

import com.mt.identityaccess.application.command.AppCreatePendingUserCommand;
import com.mt.identityaccess.application.representation.AppPendingUserCardRep;
import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.identityaccess.port.adapter.service.HttpActivationCodeAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppPendingUserApplicationService extends RoleBasedRestfulService<PendingUser, AppPendingUserCardRep, Void, VoidTypedClass> {
    {
        entityClass = PendingUser.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Autowired
    private HttpActivationCodeAdapter activationCodeEmailService;
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
        activationCodeEmailService.sendActivationCode(pendingBizUser.getActivationCode(), pendingBizUser.getEmail());
        return pendingBizUser;
    }

}
