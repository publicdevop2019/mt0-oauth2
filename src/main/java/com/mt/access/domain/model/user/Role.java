package com.mt.access.domain.model.user;

import com.mt.common.domain.model.sql.converter.EnumSetConverter;

public enum Role {
    ROLE_ADMIN,
    ROLE_ROOT,
    ROLE_USER;

    public static class ResourceOwnerAuthorityConverter extends EnumSetConverter<Role> {
        public ResourceOwnerAuthorityConverter() {
            super(Role.class);
        }
    }

}
