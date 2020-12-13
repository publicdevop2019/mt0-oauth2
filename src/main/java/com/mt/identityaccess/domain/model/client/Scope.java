package com.mt.identityaccess.domain.model.client;

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
