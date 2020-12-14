package com.hw.domain.model.client;

import com.hw.port.adapter.service.EnumSetConverter;

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
