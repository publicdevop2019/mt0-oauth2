package com.hw.unit.clazz.eenum;

import com.mt.identityaccess.domain.model.client.Scope;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScopeEnumTest {
    @Test
    public void ScopeEnumConverter_to_db_column() {
        List<Scope> values = List.of(Scope.READ, Scope.WRITE);
        Scope.ScopeSetConverter scopeConverter = new Scope.ScopeSetConverter();
        String s = scopeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = Scope.READ.toString() + "," + Scope.WRITE.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void ScopeEnumConverter_to_db_column_swap_order() {
        List<Scope> values = List.of(Scope.WRITE, Scope.READ);
        Scope.ScopeSetConverter scopeConverter = new Scope.ScopeSetConverter();
        String s = scopeConverter.convertToDatabaseColumn(new HashSet<>(values));
        String collect = Scope.READ.toString() + "," + Scope.WRITE.toString();
        Assert.assertEquals(collect, s);
    }

    @Test
    public void ScopeEnumConverter_to_entity_attribute() {
        String collect = Scope.WRITE.toString() + "," + Scope.READ.toString();
        Scope.ScopeSetConverter scopeConverter = new Scope.ScopeSetConverter();
        Set<Scope> set = scopeConverter.convertToEntityAttribute(collect);
        Assert.assertEquals(2, set.size());
    }
}