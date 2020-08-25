package com.hw.unit.clazz.eenum;

import com.hw.aggregate.client.model.ScopeEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScopeEnumTest {
    @Test
    public void ScopeEnumConverter_to_db_column() {
        List<ScopeEnum> values = List.of(ScopeEnum.read, ScopeEnum.write);
        ScopeEnum.ScopeSetConverter scopeConverter = new ScopeEnum.ScopeSetConverter();
        String s = scopeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = ScopeEnum.read.toString() + "," + ScopeEnum.write.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void ScopeEnumConverter_to_db_column_swap_order() {
        List<ScopeEnum> values = List.of(ScopeEnum.write, ScopeEnum.read);
        ScopeEnum.ScopeSetConverter scopeConverter = new ScopeEnum.ScopeSetConverter();
        String s = scopeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = ScopeEnum.read.toString() + "," + ScopeEnum.write.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void ScopeEnumConverter_to_entity_attribute() {
        String collect = ScopeEnum.write.toString() + "," + ScopeEnum.read.toString();
        ScopeEnum.ScopeSetConverter scopeConverter = new ScopeEnum.ScopeSetConverter();
        Set<ScopeEnum> set = scopeConverter.convertToEntityAttribute(collect);
        Assert.assertEquals(2, set.size());
    }
}