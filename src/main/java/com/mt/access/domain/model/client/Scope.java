package com.mt.access.domain.model.client;

import com.mt.common.domain.model.sql.converter.EnumSetConverter;

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
