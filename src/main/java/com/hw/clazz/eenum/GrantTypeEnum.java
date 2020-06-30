package com.hw.clazz.eenum;

import com.hw.converter.EnumSetConverter;

public enum GrantTypeEnum {
    client_credentials,
    password,
    refresh_token,
    authorization_code;

    public static class GrantTypeSetConverter extends EnumSetConverter {
        public GrantTypeSetConverter() {
            super(GrantTypeEnum.class);
        }
    }
}
