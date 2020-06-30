package com.hw.shared;

import javax.persistence.AttributeConverter;

public class EnumDBConverter<E extends Enum<E>> implements AttributeConverter<E, String> {
    private final Class<E> type;

    public EnumDBConverter(Class<E> type) {
        this.type = type;
    }

    @Override
    public String convertToDatabaseColumn(E orderState) {
        return orderState.name();
    }

    @Override
    public E convertToEntityAttribute(String s) {
        return E.valueOf(type, s);
    }
}
