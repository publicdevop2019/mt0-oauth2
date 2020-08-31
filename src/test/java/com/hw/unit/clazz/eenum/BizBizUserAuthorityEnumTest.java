package com.hw.unit.clazz.eenum;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BizBizUserAuthorityEnumTest {

//    @Test
//    public void clientAuthorityConverter_convert_to_db_column() {
//        GrantedAuthorityImpl<BizUserAuthorityEnum> tGrantedAuthority = new GrantedAuthorityImpl<>(BizUserAuthorityEnum.ROLE_ROOT);
//        GrantedAuthorityImpl<BizUserAuthorityEnum> tGrantedAuthority1 = new GrantedAuthorityImpl<>(BizUserAuthorityEnum.ROLE_ADMIN);
//        GrantedAuthorityImpl<BizUserAuthorityEnum> tGrantedAuthority2 = new GrantedAuthorityImpl<>(BizUserAuthorityEnum.ROLE_USER);
//        ArrayList<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>(List.of(tGrantedAuthority, tGrantedAuthority1, tGrantedAuthority2));
//
//        BizUserAuthorityEnum.ResourceOwnerAuthorityConverter resourceOwnerAuthorityConverter = new BizUserAuthorityEnum.ResourceOwnerAuthorityConverter();
//        String s = resourceOwnerAuthorityConverter.convertToDatabaseColumn(grantedAuthorities);
//        String expected = String.join(",", BizUserAuthorityEnum.ROLE_ROOT.toString(),
//                BizUserAuthorityEnum.ROLE_ADMIN.toString(), BizUserAuthorityEnum.ROLE_USER.toString());
//        Assert.assertEquals(expected, s);
//    }
//
//    @Test
//    public void clientAuthorityConverter_convert_to_entity_property() {
//        String dbColumn = String.join(",", BizUserAuthorityEnum.ROLE_ROOT.toString(),
//                BizUserAuthorityEnum.ROLE_ADMIN.toString(), BizUserAuthorityEnum.ROLE_USER.toString());
//
//        BizUserAuthorityEnum.ResourceOwnerAuthorityConverter resourceOwnerAuthorityConverter = new BizUserAuthorityEnum.ResourceOwnerAuthorityConverter();
//        List<GrantedAuthorityImpl> list = resourceOwnerAuthorityConverter.convertToEntityAttribute(dbColumn);
//        Assert.assertEquals(3, list.size());
//
//    }


}