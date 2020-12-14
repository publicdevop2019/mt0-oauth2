package com.hw.application;

import com.hw.application.client.ClientApplicationService;
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
    public void setAuthenticationApplicationService(AuthenticationApplicationService authenticationApplicationService) {
        ApplicationServiceRegistry.authenticationApplicationService = authenticationApplicationService;
    }

    @Autowired
    public void setClientIdempotentApplicationService(ClientIdempotentApplicationService clientIdempotentApplicationService) {
        ApplicationServiceRegistry.clientIdempotentApplicationService = clientIdempotentApplicationService;
    }

    private static AuthorizeCodeApplicationService authorizeCodeApplicationService;
    private static AuthenticationApplicationService authenticationApplicationService;
    private static ClientIdempotentApplicationService clientIdempotentApplicationService;

    public static ClientApplicationService clientApplicationService() {
        return clientApplicationService;
    }

    public static AuthorizeCodeApplicationService authorizeCodeApplicationService() {
        return authorizeCodeApplicationService;
    }

    public static AuthenticationApplicationService authenticationApplicationService() {
        return authenticationApplicationService;
    }

    public static ClientIdempotentApplicationService clientIdempotentApplicationService() {
        return clientIdempotentApplicationService;
    }
}
