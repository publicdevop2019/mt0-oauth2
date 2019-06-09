package com.hw.converter;

import com.hw.clazz.GrantedAuthorityImpl;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GrantedAuthorityConverter implements AttributeConverter<List<GrantedAuthority>, String> {

    @Override
    public String convertToDatabaseColumn(List<GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream().filter(e -> e != null).map(e -> e.getAuthority()).collect(Collectors.joining(","));
    }

    @Override
    public List<GrantedAuthority> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(",")).map(e -> GrantedAuthorityImpl.getGrantedAuthority(e)).collect(Collectors.toList());
    }
}
