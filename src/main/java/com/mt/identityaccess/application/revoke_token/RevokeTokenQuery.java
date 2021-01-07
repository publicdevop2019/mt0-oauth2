package com.mt.identityaccess.application.revoke_token;

import lombok.Getter;

public class RevokeTokenQuery {
    @Getter
    private String value;

    public RevokeTokenQuery(String queryParam) {
        this.value = queryParam;
    }

}
