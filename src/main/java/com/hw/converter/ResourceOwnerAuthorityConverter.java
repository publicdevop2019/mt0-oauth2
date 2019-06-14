package com.hw.converter;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.ResourceOwnerRoleEnum;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceOwnerAuthorityConverter implements AttributeConverter<List<GrantedAuthorityImpl<ResourceOwnerRoleEnum>>, String> {
    @Override
    public String convertToDatabaseColumn(List<GrantedAuthorityImpl<ResourceOwnerRoleEnum>> grantedAuthorities) {
        return grantedAuthorities.stream().filter(Objects::nonNull).map(GrantedAuthorityImpl::getAuthority).collect(Collectors.joining(","));
    }

    @Override
    public List<GrantedAuthorityImpl<ResourceOwnerRoleEnum>> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(",")).map(e -> GrantedAuthorityImpl.getGrantedAuthority(ResourceOwnerRoleEnum.class, e)).collect(Collectors.toList());
    }
}
