package com.hw.aggregate.client.model;

public enum ScopeEnum {
    write,
    read,
    trust;

    public static class ScopeSetConverter extends EnumSetConverter {
        public ScopeSetConverter() {
            super(ScopeEnum.class);
        }
    }
}
