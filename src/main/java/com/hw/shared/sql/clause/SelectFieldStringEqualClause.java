package com.hw.shared.sql.clause;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectFieldStringEqualClause<T> extends SelectFieldStringLikeClause<T> {
    public SelectFieldStringEqualClause(String fieldName) {
        super(fieldName);
    }

    protected Predicate getExpression(String input, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> abstractQuery) {
        return cb.equal(root.get(entityFieldName).as(String.class), input);
    }

}
