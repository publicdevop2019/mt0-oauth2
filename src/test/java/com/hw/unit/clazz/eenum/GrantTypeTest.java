package com.hw.unit.clazz.eenum;

import com.mt.identityaccess.domain.model.client.GrantType;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrantTypeTest {
    @Test
    public void grantTypeEnumConverter_to_db_column() {
        List<GrantType> values = List.of(GrantType.PASSWORD, GrantType.AUTHORIZATION_CODE);
        GrantType.GrantTypeSetConverter grantTypeConverter = new GrantType.GrantTypeSetConverter();
        String s = grantTypeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = GrantType.PASSWORD.toString() + "," + GrantType.AUTHORIZATION_CODE.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void grantTypeEnumConverter_to_db_column_swap_order() {
        List<GrantType> values = List.of(GrantType.AUTHORIZATION_CODE, GrantType.PASSWORD);
        GrantType.GrantTypeSetConverter grantTypeConverter = new GrantType.GrantTypeSetConverter();
        String s = grantTypeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = GrantType.PASSWORD.toString() + "," + GrantType.AUTHORIZATION_CODE.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void grantTypeEnumConverter_to_entity_attribute() {
        String collect = GrantType.PASSWORD.toString() + "," + GrantType.AUTHORIZATION_CODE.toString();
        GrantType.GrantTypeSetConverter grantTypeConverter = new GrantType.GrantTypeSetConverter();
        Set<GrantType> set = grantTypeConverter.convertToEntityAttribute(collect);
        Assert.assertEquals(2, set.size());
    }
}