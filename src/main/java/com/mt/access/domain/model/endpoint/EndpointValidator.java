package com.mt.access.domain.model.endpoint;


import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class EndpointValidator {
    private final Endpoint endpoint;
    private final ValidationNotificationHandler handler;

    public EndpointValidator(Endpoint client, ValidationNotificationHandler handler) {
        this.endpoint = client;
        this.handler = handler;
    }

    protected void validate() {
        userOnlyAndClientOnly();
        websocket();
        httpMethod();
    }

    private void httpMethod() {
        if (!endpoint.isWebsocket()) {
            if (endpoint.getMethod() == null || endpoint.getMethod().isBlank())
                handler.handleError("non websocket endpoints must have method");
        }
    }

    private void websocket() {
        if (endpoint.isWebsocket()) {
            if (!endpoint.isUserOnly() || endpoint.isClientOnly()) {
                handler.handleError("websocket can only be access by user");
            }
        }
    }

    private void userOnlyAndClientOnly() {
        if (endpoint.isUserOnly() && endpoint.isClientOnly()) {
            handler.handleError("userOnly and clientOnly can not be true at same time");
        }
        if (endpoint.isUserOnly()) {
            if (endpoint.getClientRoles() != null && !endpoint.getClientRoles().isEmpty()) {
                handler.handleError("if userOnly is set to true then client role should not present");
            }
        }
        if (endpoint.isClientOnly()) {
            if (endpoint.getUserRoles() != null && !endpoint.getUserRoles().isEmpty()) {
                handler.handleError("if clientOnly is set to true then user role should not present");
            }
        }
    }
}
