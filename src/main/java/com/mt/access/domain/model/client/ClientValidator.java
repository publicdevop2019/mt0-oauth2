package com.mt.access.domain.model.client;


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
        encryptedSecret();
        tokenAndGrantType();
    }

    private void tokenAndGrantType() {
        if (client.getGrantTypes() != null && !client.getGrantTypes().isEmpty()) {
            if (client.getTokenDetail().getAccessTokenValiditySeconds() == null || client.getTokenDetail().getAccessTokenValiditySeconds() < 60)
                handler.handleError("when grant present access token validity seconds must be valid");
            if (client.getGrantTypes().contains(GrantType.REFRESH_TOKEN)) {
                if (client.getTokenDetail().getRefreshTokenValiditySeconds() == null || client.getTokenDetail().getRefreshTokenValiditySeconds() < 120)
                    handler.handleError("refresh grant must has valid refresh token validity seconds");
            }
        }
    }

    private void encryptedSecret() {
        if (client.getSecret() == null)
            handler.handleError("client secret required");
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
