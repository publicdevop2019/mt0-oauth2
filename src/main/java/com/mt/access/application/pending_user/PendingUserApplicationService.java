package com.mt.access.application.pending_user;

import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.ActivationCode;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PendingUserApplicationService {
    @SubscribeForEvent
    @Transactional
    public String create(PendingUserCreateCommand command, String operationId) {
        RegistrationEmail registrationEmail = new RegistrationEmail(command.getEmail());
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, registrationEmail,
                () -> DomainRegistry.getPendingUserService().createOrUpdatePendingUser(registrationEmail, new ActivationCode()), PendingUser.class
        );
    }
}
