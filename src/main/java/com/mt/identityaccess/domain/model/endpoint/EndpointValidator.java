package com.mt.identityaccess.domain.model.endpoint;


import com.mt.common.validate.ValidationNotificationHandler;

public class EndpointValidator {
    private final Endpoint endpoint;
    private final ValidationNotificationHandler handler;

    public EndpointValidator(Endpoint client, ValidationNotificationHandler handler) {
        this.endpoint = client;
        this.handler = handler;
    }

    protected void validate() {
        userOnlyAndClientOnly();
    }

    private void userOnlyAndClientOnly() {
        if (endpoint.isUserOnly()&& endpoint.isClientOnly()) {
            handler.handleError("userOnly and clientOnly can not be true at same time");
        }
    }
}
