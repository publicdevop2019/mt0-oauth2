package com.hw.shared.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectEqualClause<T> extends WhereClause<T> {
    private final String targetValue;

    public SelectEqualClause(String fieldName, String equalTo) {
        entityFieldName = fieldName;
        targetValue = equalTo;
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        return cb.equal(root.get(entityFieldName), targetValue);
    }
}
