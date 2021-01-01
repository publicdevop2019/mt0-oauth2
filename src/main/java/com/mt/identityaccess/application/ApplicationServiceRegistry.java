package com.mt.identityaccess.application;

import com.mt.common.application.ApplicationServiceIdempotentWrapper;
import com.mt.identityaccess.application.client.ClientApplicationService;
import com.mt.identityaccess.application.client.EndpointApplicationService;
import com.mt.identityaccess.application.pending_user.PendingUserApplicationService;
import com.mt.identityaccess.application.revoke_token.RevokeTokenApplicationService;
import com.mt.identityaccess.application.user.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    private static ClientApplicationService clientApplicationService;
    private static PendingUserApplicationService pendingUserApplicationService;
    private static UserApplicationService userApplicationService;
    private static EndpointApplicationService endpointApplicationService;
    private static RevokeTokenApplicationService revokeTokenApplicationService;

    public static UserApplicationService userApplicationService() {
        return userApplicationService;
    }

    public static RevokeTokenApplicationService revokeTokenApplicationService() {
        return revokeTokenApplicationService;
    }

    public static EndpointApplicationService endpointApplicationService() {
        return endpointApplicationService;
    }

    @Autowired
    public void setRevokeTokenApplicationService(RevokeTokenApplicationService revokeTokenApplicationService) {
        ApplicationServiceRegistry.revokeTokenApplicationService = revokeTokenApplicationService;
    }

    @Autowired
    public void setEndpointApplicationService(EndpointApplicationService endpointApplicationService) {
        ApplicationServiceRegistry.endpointApplicationService = endpointApplicationService;
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
