package com.mt.identityaccess.application;

import com.mt.identityaccess.application.client.ClientApplicationService;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationServiceRegistry {
    @Autowired
    private static ClientApplicationService clientApplicationService;
    @Autowired
    private static AuthorizeCodeApplicationService authorizeCodeApplicationService;
    @Autowired
    private static AuthenticationApplicationService authenticationApplicationService;
    @Autowired
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
