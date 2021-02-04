package com.mt.identityaccess.domain.model.client;

import com.mt.common.persistence.EnumSetConverter;

public enum Scope {
    WRITE,
    READ,
    TRUST;
    public static class DBConverter extends EnumSetConverter<Scope> {
        public DBConverter() {
            super(Scope.class);
        }
    }
}
