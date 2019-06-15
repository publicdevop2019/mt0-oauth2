package com.hw.clazz.eenum;

import com.hw.converter.EnumConverter;

public enum GrantTypeEnum {
    client_credentials,
    password,
    refresh_token,
    authorization_code;

    public static class GrantTypeConverter extends EnumConverter {
        public GrantTypeConverter() {
            super(GrantTypeEnum.class);
        }
    }
}
