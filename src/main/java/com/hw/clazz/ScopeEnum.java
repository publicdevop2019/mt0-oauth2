package com.hw.clazz;

import com.hw.converter.EnumConverter;

public enum ScopeEnum {
    write,
    read,
    trust;

    public static class ScopeConverter extends EnumConverter {
        public ScopeConverter() {
            super(ScopeEnum.class);
        }
    }
}
