package com.hw.shared.sql.clause;

import com.hw.shared.sql.exception.UnsupportedQueryException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SelectFieldNumberRangeClause<T> extends WhereClause<T> {
    public SelectFieldNumberRangeClause(String fieldName) {
        entityFieldName = fieldName;
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        String[] split = query.split("\\$");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            if (str.contains("<=")) {
                int i = Integer.parseInt(str.replace("<=", ""));
                results.add(cb.lessThanOrEqualTo(root.get(entityFieldName), i));
            } else if (str.contains(">=")) {
                int i = Integer.parseInt(str.replace(">=", ""));
                results.add(cb.greaterThanOrEqualTo(root.get(entityFieldName), i));
            } else if (str.contains("<")) {
                int i = Integer.parseInt(str.replace("<", ""));
                results.add(cb.lessThan(root.get(entityFieldName), i));
            } else if (str.contains(">")) {
                int i = Integer.parseInt(str.replace(">", ""));
                results.add(cb.greaterThan(root.get(entityFieldName), i));
            } else {
                throw new UnsupportedQueryException();
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }
}
