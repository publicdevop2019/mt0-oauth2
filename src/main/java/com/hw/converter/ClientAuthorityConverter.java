package com.hw.converter;

import com.hw.clazz.ClientRoleEnum;
import com.hw.clazz.GrantedAuthorityImpl;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientAuthorityConverter implements AttributeConverter<List<GrantedAuthorityImpl<ClientRoleEnum>>, String> {
    @Override
    public String convertToDatabaseColumn(List<GrantedAuthorityImpl<ClientRoleEnum>> grantedAuthorities) {
        return grantedAuthorities.stream().filter(Objects::nonNull).map(GrantedAuthorityImpl::getAuthority).collect(Collectors.joining(","));
    }

    @Override
    public List<GrantedAuthorityImpl<ClientRoleEnum>> convertToEntityAttribute(String s) {
        return  Arrays.stream(s.split(",")).map(e -> GrantedAuthorityImpl.getGrantedAuthority(ClientRoleEnum.class, e)).collect(Collectors.toList());
    }
}
