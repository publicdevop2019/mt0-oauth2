package com.mt.identityaccess.domain.model.user;

import com.mt.identityaccess.domain.model.client.EnumSetConverter;

public enum BizUserAuthorityEnum {
    ROLE_ADMIN,
    ROLE_ROOT,
    ROLE_USER;

    public static class ResourceOwnerAuthorityConverter extends EnumSetConverter<BizUserAuthorityEnum> {
        public ResourceOwnerAuthorityConverter() {
            super(BizUserAuthorityEnum.class);
        }
    }

}
