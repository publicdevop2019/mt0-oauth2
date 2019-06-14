package com.hw.clazz;

import com.hw.converter.DefaultAuthorityConverter;

public enum ResourceOwnerAuthorityEnum {
    ROLE_ADMIN,
    ROLE_ROOT,
    ROLE_USER;

    public static class ResourceOwnerAuthorityConverter extends DefaultAuthorityConverter {
        public ResourceOwnerAuthorityConverter() {
            super(ResourceOwnerAuthorityEnum.class);
        }
    }

}
