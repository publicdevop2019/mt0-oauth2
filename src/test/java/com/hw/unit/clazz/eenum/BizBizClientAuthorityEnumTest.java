package com.hw.unit.clazz.eenum;

import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BizBizClientAuthorityEnumTest {

//    @Test
//    public void clientAuthorityConverter_convert_to_db_column() {
//        GrantedAuthorityImpl<BizClientAuthorityEnum> tGrantedAuthority = new GrantedAuthorityImpl<>(BizClientAuthorityEnum.ROLE_THIRD_PARTY);
//        GrantedAuthorityImpl<BizClientAuthorityEnum> tGrantedAuthority1 = new GrantedAuthorityImpl<>(BizClientAuthorityEnum.ROLE_BACKEND);
//        GrantedAuthorityImpl<BizClientAuthorityEnum> tGrantedAuthority2 = new GrantedAuthorityImpl<>(BizClientAuthorityEnum.ROLE_FRONTEND);
//        ArrayList<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>(List.of(tGrantedAuthority, tGrantedAuthority1, tGrantedAuthority2));
//
//        BizClientAuthorityEnum.ClientAuthorityConverter clientAuthorityConverter = new BizClientAuthorityEnum.ClientAuthorityConverter();
////        String s = clientAuthorityConverter.convertToDatabaseColumn(grantedAuthorities);
//        String expected = String.join(",", BizClientAuthorityEnum.ROLE_THIRD_PARTY.toString(),
//                BizClientAuthorityEnum.ROLE_BACKEND.toString(), BizClientAuthorityEnum.ROLE_FRONTEND.toString());
//        Assert.assertEquals(expected, s);
//    }
//
//    @Test
//    public void clientAuthorityConverter_convert_to_entity_property() {
//        String dbColumn = String.join(",", BizClientAuthorityEnum.ROLE_THIRD_PARTY.toString(),
//                BizClientAuthorityEnum.ROLE_BACKEND.toString(), BizClientAuthorityEnum.ROLE_FRONTEND.toString());
//
//        BizClientAuthorityEnum.ClientAuthorityConverter clientAuthorityConverter = new BizClientAuthorityEnum.ClientAuthorityConverter();
////        List<GrantedAuthorityImpl> list = clientAuthorityConverter.convertToEntityAttribute(dbColumn);
//        Assert.assertEquals(3, list.size());
//
//    }

}