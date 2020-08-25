package com.hw.shared.rest;

import lombok.Data;

@Data
public abstract class TypedClass<T> {
    private Class<T> clazz;

    public TypedClass(Class<T> clazz) {
        this.clazz = clazz;
    }
}
