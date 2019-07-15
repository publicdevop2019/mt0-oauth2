package com.hw.unit.clazz.eenum;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ResourceOwnerAuthorityEnumTest {

    @Test
    public void clientAuthorityConverter_convert_to_db_column() {
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> tGrantedAuthority = new GrantedAuthorityImpl<>(ResourceOwnerAuthorityEnum.ROLE_ROOT);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> tGrantedAuthority1 = new GrantedAuthorityImpl<>(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> tGrantedAuthority2 = new GrantedAuthorityImpl<>(ResourceOwnerAuthorityEnum.ROLE_USER);
        ArrayList<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>(List.of(tGrantedAuthority, tGrantedAuthority1, tGrantedAuthority2));

        ResourceOwnerAuthorityEnum.ResourceOwnerAuthorityConverter resourceOwnerAuthorityConverter = new ResourceOwnerAuthorityEnum.ResourceOwnerAuthorityConverter();
        String s = resourceOwnerAuthorityConverter.convertToDatabaseColumn(grantedAuthorities);
        String expected = String.join(",", ResourceOwnerAuthorityEnum.ROLE_ROOT.toString(),
                ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString(), ResourceOwnerAuthorityEnum.ROLE_USER.toString());
        Assert.assertEquals(expected, s);
    }

    @Test
    public void clientAuthorityConverter_convert_to_entity_property() {
        String dbColumn = String.join(",", ResourceOwnerAuthorityEnum.ROLE_ROOT.toString(),
                ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString(), ResourceOwnerAuthorityEnum.ROLE_USER.toString());

        ResourceOwnerAuthorityEnum.ResourceOwnerAuthorityConverter resourceOwnerAuthorityConverter = new ResourceOwnerAuthorityEnum.ResourceOwnerAuthorityConverter();
        List<GrantedAuthorityImpl> list = resourceOwnerAuthorityConverter.convertToEntityAttribute(dbColumn);
        Assert.assertEquals(3, list.size());

    }


}