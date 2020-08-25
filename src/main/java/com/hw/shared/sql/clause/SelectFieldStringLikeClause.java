package com.hw.shared.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectFieldStringLikeClause<T> extends WhereClause<T> {
    public SelectFieldStringLikeClause(String fieldName) {
        entityFieldName = fieldName;
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        return cb.like(root.get(entityFieldName), "%" + query + "%");
    }
}
