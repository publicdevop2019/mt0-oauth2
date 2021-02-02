package com.mt.identityaccess.domain.model.client;


import com.mt.common.validate.ValidationNotificationHandler;

public class ClientValidator {
    private final Client client;
    private final ValidationNotificationHandler handler;

    public ClientValidator(Client client, ValidationNotificationHandler handler) {
        this.client = client;
        this.handler = handler;
    }

    protected void validate() {
        accessAndAuthorities();
    }

    private void accessAndAuthorities() {
        if (client.isAccessible()) {
            if (
                    client.getAuthorities().stream().noneMatch(e -> e.equals(Authority.ROLE_BACKEND))
                            || client.getAuthorities().stream().noneMatch(e -> e.equals(Authority.ROLE_FIRST_PARTY))
            ) {
                handler.handleError("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
            }
        }
    }
}
