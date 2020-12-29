package com.mt.identityaccess.domain.model.client;

import com.mt.common.infrastructure.EnumSetConverter;

public enum Scope {
    WRITE,
    READ,
    TRUST;
    public static class ScopeConverter extends EnumSetConverter<Scope> {
        public ScopeConverter() {
            super(Scope.class);
        }
    }
}
