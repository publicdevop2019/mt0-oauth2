package com.hw.aggregate.user.model;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultAuthorityConverter<T extends Enum<T>> implements AttributeConverter<List<GrantedAuthorityImpl<T>>, String> {

    private final Class<T> type;

    public DefaultAuthorityConverter(Class<T> type) {
        this.type = type;
    }

    @Override
    public String convertToDatabaseColumn(List<GrantedAuthorityImpl<T>> grantedAuthorities) {
        return grantedAuthorities.stream().filter(Objects::nonNull).map(e -> e.getGrantedAuthority().toString()).collect(Collectors.joining(","));
    }

    @Override
    public List<GrantedAuthorityImpl<T>> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(",")).map(e -> GrantedAuthorityImpl.getGrantedAuthority(type, e)).collect(Collectors.toList());
    }

}
