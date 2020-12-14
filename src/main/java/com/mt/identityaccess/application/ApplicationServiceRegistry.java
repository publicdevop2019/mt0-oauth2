package com.mt.identityaccess.application;

import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationServiceRegistry {
    @Autowired
    private static ClientApplicationService clientApplicationService;
    @Autowired
    private static AuthorizeCodeApplicationService authorizeCodeApplicationService;

    public static ClientApplicationService clientApplicationService() {
        return clientApplicationService;
    }
    public static AuthorizeCodeApplicationService authorizeCodeApplicationService() {
        return authorizeCodeApplicationService;
    }
}
