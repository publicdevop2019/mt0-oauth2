package com.hw.aggregate.user.model;

import com.hw.aggregate.client.model.EnumSetConverter;

public enum BizUserAuthorityEnum {
    ROLE_ADMIN,
    ROLE_ROOT,
    ROLE_USER;

    public static class ResourceOwnerAuthorityConverter extends EnumSetConverter {
        public ResourceOwnerAuthorityConverter() {
            super(BizUserAuthorityEnum.class);
        }
    }

}
