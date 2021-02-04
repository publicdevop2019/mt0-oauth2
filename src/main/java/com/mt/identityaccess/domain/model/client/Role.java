package com.mt.identityaccess.domain.model.client;


import com.mt.common.persistence.EnumSetConverter;

public enum Role {
    ROLE_FRONTEND,
    ROLE_BACKEND,
    ROLE_FIRST_PARTY,
    ROLE_THIRD_PARTY,
    ROLE_TRUST,
    ROLE_ROOT;

    public static class DBConverter extends EnumSetConverter<Role> {
        public DBConverter() {
            super(Role.class);
        }
    }

}
