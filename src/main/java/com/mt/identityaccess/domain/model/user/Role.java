package com.mt.identityaccess.domain.model.user;

import com.mt.common.persistence.EnumSetConverter;

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
