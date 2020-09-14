package com.hw.aggregate.client.model;

public enum ScopeEnum {
    WRITE,
    READ,
    TRUST;

    public static class ScopeSetConverter extends EnumSetConverter<ScopeEnum> {
        public ScopeSetConverter() {
            super(ScopeEnum.class);
        }
    }
}
