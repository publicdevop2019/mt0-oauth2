package com.hw.clazz.eenum;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrantTypeEnumTest {
    @Test
    public void grantTypeEnumConverter_to_db_column() {
        List<GrantTypeEnum> values = List.of(GrantTypeEnum.password, GrantTypeEnum.authorization_code);
        GrantTypeEnum.GrantTypeConverter grantTypeConverter = new GrantTypeEnum.GrantTypeConverter();
        String s = grantTypeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = GrantTypeEnum.password.toString() + "," + GrantTypeEnum.authorization_code.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void grantTypeEnumConverter_to_db_column_swap_order() {
        List<GrantTypeEnum> values = List.of(GrantTypeEnum.authorization_code, GrantTypeEnum.password);
        GrantTypeEnum.GrantTypeConverter grantTypeConverter = new GrantTypeEnum.GrantTypeConverter();
        String s = grantTypeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = GrantTypeEnum.password.toString() + "," + GrantTypeEnum.authorization_code.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void grantTypeEnumConverter_to_entity_attribute() {
        String collect = GrantTypeEnum.password.toString() + "," + GrantTypeEnum.authorization_code.toString();
        GrantTypeEnum.GrantTypeConverter grantTypeConverter = new GrantTypeEnum.GrantTypeConverter();
        Set<GrantTypeEnum> set = grantTypeConverter.convertToEntityAttribute(collect);
        Assert.assertEquals(2, set.size());
    }
}