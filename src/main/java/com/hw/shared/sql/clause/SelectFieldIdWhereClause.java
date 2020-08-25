package com.hw.shared.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

public class SelectFieldIdWhereClause<T> extends WhereClause<T> {
    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root) {
        String[] split = query.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(COMMON_ENTITY_ID), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
