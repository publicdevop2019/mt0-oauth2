package com.hw.unit.clazz.eenum;

import com.mt.identityaccess.domain.model.client.ScopeEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScopeEnumTest {
    @Test
    public void ScopeEnumConverter_to_db_column() {
        List<ScopeEnum> values = List.of(ScopeEnum.READ, ScopeEnum.WRITE);
        ScopeEnum.ScopeSetConverter scopeConverter = new ScopeEnum.ScopeSetConverter();
        String s = scopeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = ScopeEnum.READ.toString() + "," + ScopeEnum.WRITE.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void ScopeEnumConverter_to_db_column_swap_order() {
        List<ScopeEnum> values = List.of(ScopeEnum.WRITE, ScopeEnum.READ);
        ScopeEnum.ScopeSetConverter scopeConverter = new ScopeEnum.ScopeSetConverter();
        String s = scopeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = ScopeEnum.READ.toString() + "," + ScopeEnum.WRITE.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void ScopeEnumConverter_to_entity_attribute() {
        String collect = ScopeEnum.WRITE.toString() + "," + ScopeEnum.READ.toString();
        ScopeEnum.ScopeSetConverter scopeConverter = new ScopeEnum.ScopeSetConverter();
        Set<ScopeEnum> set = scopeConverter.convertToEntityAttribute(collect);
        Assert.assertEquals(2, set.size());
    }
}