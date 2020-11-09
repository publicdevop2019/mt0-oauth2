package com.hw.shared.sql.clause;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectFieldEmailEqualClause<T> extends WhereClause<T> {
    public SelectFieldEmailEqualClause(String fieldName) {
        entityFieldName = fieldName;
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> abstractQuery) {
        return cb.equal(root.get(entityFieldName), query);
    }
}
