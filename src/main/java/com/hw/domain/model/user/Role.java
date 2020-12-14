package com.hw.domain.model.user;

import com.hw.port.adapter.service.EnumSetConverter;

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
