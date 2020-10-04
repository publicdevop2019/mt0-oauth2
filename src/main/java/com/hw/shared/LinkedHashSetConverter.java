package com.hw.shared;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class LinkedHashSetConverter implements AttributeConverter<LinkedHashSet<String>, String> {

    @Override
    public String convertToDatabaseColumn(LinkedHashSet<String> strings) {
        if (ObjectUtils.isEmpty(strings))
            return null;
        return String.join(",", strings);
    }

    @Override
    public LinkedHashSet<String> convertToEntityAttribute(String s) {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        if (StringUtils.hasText(s)) {
            List<String> collect = Arrays.stream(s.split(",")).collect(Collectors.toList());
            linkedHashSet.addAll(collect);
            return linkedHashSet;
        }
        return linkedHashSet;
    }
}
