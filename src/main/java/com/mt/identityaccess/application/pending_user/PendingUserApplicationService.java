package com.mt.identityaccess.application.pending_user;

import com.mt.common.application.SubscribeForEvent;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PendingUserApplicationService {
    @SubscribeForEvent
    @Transactional
    public RegistrationEmail create(AppCreatePendingUserCommand command, String operationId) {
        RegistrationEmail registrationEmail = new RegistrationEmail(command.getEmail());
        return (RegistrationEmail) ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, registrationEmail,
                () -> DomainRegistry.pendingUserService().createOrUpdatePendingUser(registrationEmail, new ActivationCode())
        );
    }
}
