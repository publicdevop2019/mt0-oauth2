package com.hw.clazz.eenum;

import com.hw.converter.EnumSetConverter;

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
