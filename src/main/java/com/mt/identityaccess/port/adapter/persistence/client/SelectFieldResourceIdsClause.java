package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.domain.model.sql.clause.WhereClause;
import com.mt.identityaccess.domain.model.client.Client;

import javax.persistence.criteria.*;

public class SelectFieldResourceIdsClause extends WhereClause<Client> {
    public SelectFieldResourceIdsClause() {
        super();
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
        Join<Object, Object> tags = root.join("resources");
        CriteriaBuilder.In<Object> clause = cb.in(tags.get("domainId"));
        String[] split = query.split("\\$");
        for (String str : split) {
            clause.value(str);
        }
        return clause;
    }
}
