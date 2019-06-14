package com.hw.clazz;

import com.hw.converter.DefaultAuthorityConverter;

public enum ClientAuthorityEnum {
    ROLE_FRONTEND,
    ROLE_BACKEND,
    ROLE_FIRST_PARTY,
    ROLE_THIRD_PARTY,
    ROLE_TRUST;

    public static class ClientAuthorityConverter extends DefaultAuthorityConverter {
        public ClientAuthorityConverter() {
            super(ClientAuthorityEnum.class);
        }
    }
}
