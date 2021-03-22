package com.mt.access.domain.model.client;

import com.mt.common.domain.model.validate.Validator;

public class RefreshTokenGrantValidator {
    private final RefreshTokenGrant refreshTokenGrant;

    public RefreshTokenGrantValidator(RefreshTokenGrant refreshTokenGrant) {
        this.refreshTokenGrant = refreshTokenGrant;
    }

    protected void validate() {
        tokenSeconds();
    }

    private void tokenSeconds() {
        if (refreshTokenGrant.enabled) {
            Validator.greaterThanOrEqualTo(refreshTokenGrant.getRefreshTokenValiditySeconds(), 120);
        }
    }
}
