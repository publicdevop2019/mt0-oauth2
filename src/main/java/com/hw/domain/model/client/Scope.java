package com.hw.domain.model.client;

import com.hw.port.adapter.service.EnumSetConverter;

public enum Scope {
    WRITE,
    READ,
    TRUST;

    public static class ScopeSetConverter extends EnumSetConverter<Scope> {
        public ScopeSetConverter() {
            super(Scope.class);
        }
    }
}
