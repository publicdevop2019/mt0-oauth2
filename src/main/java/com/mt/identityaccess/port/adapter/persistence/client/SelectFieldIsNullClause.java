package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.domain.model.sql.clause.FieldStringLikeClause;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectFieldIsNullClause<T> extends FieldStringLikeClause<T> {
    public SelectFieldIsNullClause(String fieldName) {
        super(fieldName);
    }

    protected Predicate getExpression(String input, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> abstractQuery) {
        return cb.isNull(root.get(entityFieldName));
    }

}
