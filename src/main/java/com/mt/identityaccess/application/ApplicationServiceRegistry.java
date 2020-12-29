package com.mt.identityaccess.application;

import com.mt.common.application.ApplicationServiceIdempotentWrapper;
import com.mt.identityaccess.application.client.ClientApplicationService;
import com.mt.identityaccess.application.pending_user.PendingUserApplicationService;
import com.mt.identityaccess.application.user.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    private static ClientApplicationService clientApplicationService;
    private static PendingUserApplicationService pendingUserApplicationService;
    private static UserApplicationService userApplicationService;

    public static UserApplicationService userApplicationService() {
        return userApplicationService;
    }

    @Autowired
    public void setUserApplicationService(UserApplicationService userApplicationService) {
        ApplicationServiceRegistry.userApplicationService = userApplicationService;
    }

    @Autowired
    public void setClientApplicationService(ClientApplicationService clientApplicationService) {
        ApplicationServiceRegistry.clientApplicationService = clientApplicationService;
    }

    @Autowired
    public void setAuthorizeCodeApplicationService(AuthorizeCodeApplicationService authorizeCodeApplicationService) {
        ApplicationServiceRegistry.authorizeCodeApplicationService = authorizeCodeApplicationService;
    }

    @Autowired
    public void setPendingUserApplicationService(PendingUserApplicationService pendingUserApplicationService) {
        ApplicationServiceRegistry.pendingUserApplicationService = pendingUserApplicationService;
    }

    @Autowired
    public void setClientIdempotentApplicationService(ApplicationServiceIdempotentWrapper clientIdempotentApplicationService) {
        ApplicationServiceRegistry.applicationServiceIdempotentWrapper = clientIdempotentApplicationService;
    }

    private static AuthorizeCodeApplicationService authorizeCodeApplicationService;
    private static ApplicationServiceIdempotentWrapper applicationServiceIdempotentWrapper;

    public static ClientApplicationService clientApplicationService() {
        return clientApplicationService;
    }

    public static AuthorizeCodeApplicationService authorizeCodeApplicationService() {
        return authorizeCodeApplicationService;
    }

    public static ApplicationServiceIdempotentWrapper idempotentWrapper() {
        return applicationServiceIdempotentWrapper;
    }

    public static PendingUserApplicationService pendingUserApplicationService() {
        return pendingUserApplicationService;
    }
}
