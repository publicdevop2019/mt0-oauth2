package com.hw.unit.clazz.eenum;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ClientAuthorityEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ClientAuthorityEnumTest {

    @Test
    public void clientAuthorityConverter_convert_to_db_column() {
        GrantedAuthorityImpl<ClientAuthorityEnum> tGrantedAuthority = new GrantedAuthorityImpl<>(ClientAuthorityEnum.ROLE_THIRD_PARTY);
        GrantedAuthorityImpl<ClientAuthorityEnum> tGrantedAuthority1 = new GrantedAuthorityImpl<>(ClientAuthorityEnum.ROLE_BACKEND);
        GrantedAuthorityImpl<ClientAuthorityEnum> tGrantedAuthority2 = new GrantedAuthorityImpl<>(ClientAuthorityEnum.ROLE_FRONTEND);
        ArrayList<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>(List.of(tGrantedAuthority, tGrantedAuthority1, tGrantedAuthority2));

        ClientAuthorityEnum.ClientAuthorityConverter clientAuthorityConverter = new ClientAuthorityEnum.ClientAuthorityConverter();
        String s = clientAuthorityConverter.convertToDatabaseColumn(grantedAuthorities);
        String expected = String.join(",", ClientAuthorityEnum.ROLE_THIRD_PARTY.toString(),
                ClientAuthorityEnum.ROLE_BACKEND.toString(), ClientAuthorityEnum.ROLE_FRONTEND.toString());
        Assert.assertEquals(expected, s);
    }

    @Test
    public void clientAuthorityConverter_convert_to_entity_property() {
        String dbColumn = String.join(",", ClientAuthorityEnum.ROLE_THIRD_PARTY.toString(),
                ClientAuthorityEnum.ROLE_BACKEND.toString(), ClientAuthorityEnum.ROLE_FRONTEND.toString());

        ClientAuthorityEnum.ClientAuthorityConverter clientAuthorityConverter = new ClientAuthorityEnum.ClientAuthorityConverter();
        List<GrantedAuthorityImpl> list = clientAuthorityConverter.convertToEntityAttribute(dbColumn);
        Assert.assertEquals(3, list.size());

    }

}