package com.mt.identityaccess.domain.model.client;

import com.mt.identityaccess.port.adapter.service.EnumSetConverter;

public enum GrantType {
    CLIENT_CREDENTIALS,
    PASSWORD,
    REFRESH_TOKEN,
    AUTHORIZATION_CODE;

    public static class GrantTypeSetConverter extends EnumSetConverter<GrantType> {
        public GrantTypeSetConverter() {
            super(GrantType.class);
        }
    }
}
