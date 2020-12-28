package com.mt.identityaccess.port.adapter.persistence;

import com.mt.common.sql.clause.WhereClause;
import com.mt.identityaccess.domain.model.client.Client;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SelectFieldClientIdEqualClause extends WhereClause<Client> {
    public SelectFieldClientIdEqualClause() {
        super();
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
        String[] split = query.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get("clientId").get("clientId"), str));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
