package com.mt.identityaccess.application;

import com.mt.common.application.ApplicationServiceIdempotentWrapper;
import com.mt.identityaccess.application.client.ClientApplicationService;
import com.mt.identityaccess.domain.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    private static ClientApplicationService clientApplicationService;

    @Autowired
    public void setClientApplicationService(ClientApplicationService clientApplicationService) {
        ApplicationServiceRegistry.clientApplicationService = clientApplicationService;
    }

    @Autowired
    public void setAuthorizeCodeApplicationService(AuthorizeCodeApplicationService authorizeCodeApplicationService) {
        ApplicationServiceRegistry.authorizeCodeApplicationService = authorizeCodeApplicationService;
    }

    @Autowired
    public void setAuthenticationApplicationService(AuthenticationService authenticationApplicationService) {
        ApplicationServiceRegistry.authenticationApplicationService = authenticationApplicationService;
    }

    @Autowired
    public void setClientIdempotentApplicationService(ApplicationServiceIdempotentWrapper clientIdempotentApplicationService) {
        ApplicationServiceRegistry.clientIdempotentApplicationService = clientIdempotentApplicationService;
    }

    private static AuthorizeCodeApplicationService authorizeCodeApplicationService;
    private static AuthenticationService authenticationApplicationService;
    private static ApplicationServiceIdempotentWrapper clientIdempotentApplicationService;

    public static ClientApplicationService clientApplicationService() {
        return clientApplicationService;
    }

    public static AuthorizeCodeApplicationService authorizeCodeApplicationService() {
        return authorizeCodeApplicationService;
    }

    public static AuthenticationService authenticationApplicationService() {
        return authenticationApplicationService;
    }

    public static ApplicationServiceIdempotentWrapper clientIdempotentApplicationService() {
        return clientIdempotentApplicationService;
    }
}
