package com.mt.identityaccess.domain.model.app;

public enum GrantTypeEnum {
    CLIENT_CREDENTIALS,
    PASSWORD,
    REFRESH_TOKEN,
    AUTHORIZATION_CODE;

    public static class GrantTypeSetConverter extends EnumSetConverter<GrantTypeEnum> {
        public GrantTypeSetConverter() {
            super(GrantTypeEnum.class);
        }
    }
}