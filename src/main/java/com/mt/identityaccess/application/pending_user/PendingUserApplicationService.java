package com.mt.identityaccess.application.pending_user;

import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.command.AppCreatePendingUserCommand;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.pending_user.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PendingUserApplicationService {
    @Transactional
    public RegistrationEmail provisionPendingUser(AppCreatePendingUserCommand command, String operationId) {
        RegistrationEmail registrationEmail = new RegistrationEmail(command.getEmail());
        return (RegistrationEmail) ApplicationServiceRegistry.idempotentWrapper().idempotentProvision(command, operationId, registrationEmail,
                () -> DomainRegistry.pendingUserService().provisionOrUpdatePendingUser(registrationEmail, new ActivationCode())
        );
    }

//    @Transactional(readOnly = true)
//    public Optional<Client> pendingUser(String id) {
//        return DomainRegistry.pendingUserRepository().
//    }
}
