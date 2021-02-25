package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.domain.model.sql.clause.WhereClause;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SelectFieldDomainIdEqualClause<T> extends WhereClause<T> {
    private String domainIdName;

    public SelectFieldDomainIdEqualClause(String domainIdName) {
        super();
        this.domainIdName = domainIdName;
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> abstractQuery) {
        String[] split = query.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(this.domainIdName).get("domainId"), str));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
