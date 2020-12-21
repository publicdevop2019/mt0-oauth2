package com.mt.identityaccess.port.adapter.persistence;

import com.mt.common.sql.clause.WhereClause;
import com.mt.identityaccess.domain.model.client.Client;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SelectFieldClientIdEqualClause extends WhereClause<Client> {
    public SelectFieldClientIdEqualClause(String s) {
        super();
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
        return cb.equal(root.get("clientId").get("clientId"), query);
    }
}
