package com.hw.aggregate.client.model;

public enum ScopeEnum {
    write,
    read,
    trust;

    public static class ScopeSetConverter extends EnumSetConverter<ScopeEnum> {
        public ScopeSetConverter() {
            super(ScopeEnum.class);
        }
    }
}
