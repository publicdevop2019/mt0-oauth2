package com.hw.aggregate.user.model;

import com.hw.shared.sql.clause.WhereClause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectFieldEmailEqualClause<T>  extends WhereClause<T> {
    public SelectFieldEmailEqualClause(String fieldName) {
        entityFieldName = fieldName;
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        return cb.equal(root.get(entityFieldName), query);
    }
}
