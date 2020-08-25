package com.hw.shared.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectFieldEnumStringEqualClause<T> extends WhereClause<T> {
    public SelectFieldEnumStringEqualClause(String typeLiteral) {
        entityFieldName = typeLiteral;
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        return cb.equal(root.get(entityFieldName).as(String.class), query);
    }
}
