package com.mt.identityaccess.application;

import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationServiceRegistry {
    @Autowired
    private static ClientApplicationService clientApplicationService;

    public static ClientApplicationService clientApplicationService() {
        return clientApplicationService;
    }
}
