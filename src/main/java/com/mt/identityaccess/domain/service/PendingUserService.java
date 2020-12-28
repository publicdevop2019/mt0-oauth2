package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.pending_user.ActivationCode;
import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import com.mt.identityaccess.domain.model.pending_user.event.PendingUserCodeUpdated;
import com.mt.identityaccess.domain.model.pending_user.event.PendingUserProvisioned;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PendingUserService {

    public RegistrationEmail provisionOrUpdatePendingUser(
            RegistrationEmail email,
            ActivationCode activationCode
    ) {
        Optional<PendingUser> pendingResourceOwner = DomainRegistry.pendingUserRepository().pendingUserOfEmail(email);
        if (pendingResourceOwner.isEmpty()) {
            PendingUser pendingUser = new PendingUser(email, activationCode);
            DomainRegistry.pendingUserRepository().add(pendingUser);
            DomainRegistry.userNotificationService().sendActivationCodeTo(email, activationCode);
            DomainEventPublisher.instance().publish(new PendingUserProvisioned(pendingUser.getRegistrationEmail()));
            return pendingUser.getRegistrationEmail();
        } else {
            pendingResourceOwner.get().newActivationCode(activationCode);
            DomainRegistry.userNotificationService().sendActivationCodeTo(email, activationCode);
            DomainEventPublisher.instance().publish(new PendingUserCodeUpdated(pendingResourceOwner.get().getRegistrationEmail()));
            return pendingResourceOwner.get().getRegistrationEmail();
        }
    }
}
