package com.mt.identityaccess.domain.model.app;

public enum BizClientAuthorityEnum {
    ROLE_FRONTEND,
    ROLE_BACKEND,
    ROLE_FIRST_PARTY,
    ROLE_THIRD_PARTY,
    ROLE_TRUST,
    /**
     * root client can not be deleted
     */
    ROLE_ROOT;

    public static class ClientAuthorityConverter extends EnumSetConverter<BizClientAuthorityEnum> {
        public ClientAuthorityConverter() {
            super(BizClientAuthorityEnum.class);
        }
    }
}
