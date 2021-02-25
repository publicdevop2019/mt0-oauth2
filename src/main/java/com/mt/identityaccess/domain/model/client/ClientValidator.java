package com.mt.identityaccess.domain.model.client;


import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class ClientValidator {
    private final Client client;
    private final ValidationNotificationHandler handler;

    public ClientValidator(Client client, ValidationNotificationHandler handler) {
        this.client = client;
        this.handler = handler;
    }

    protected void validate() {
        accessAndRoles();
    }

    private void accessAndRoles() {
        if (client.isAccessible()) {
            if (
                    client.getRoles().stream().noneMatch(e -> e.equals(Role.ROLE_BACKEND))
                            || client.getRoles().stream().noneMatch(e -> e.equals(Role.ROLE_FIRST_PARTY))
            ) {
                handler.handleError("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
            }
        }
    }
}
