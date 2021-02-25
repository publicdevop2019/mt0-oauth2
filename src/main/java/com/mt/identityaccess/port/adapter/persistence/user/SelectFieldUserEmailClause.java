package com.mt.identityaccess.port.adapter.persistence.user;

import com.mt.common.domain.model.sql.clause.WhereClause;
import com.mt.identityaccess.domain.model.user.User;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SelectFieldUserEmailClause extends WhereClause<User> {
    public SelectFieldUserEmailClause() {
        super();
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<User> root, AbstractQuery<?> abstractQuery) {
        String[] split = query.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.like(root.get("email").get("email"), "%" + str + "%"));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
