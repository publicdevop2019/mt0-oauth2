package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.sql.clause.WhereClause;
import com.mt.common.sql.exception.UnsupportedQueryException;
import com.mt.identityaccess.domain.model.client.Client;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SelectFieldGrantAccessTokenClause extends WhereClause<Client> {
    public SelectFieldGrantAccessTokenClause() {
        super();
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
        String[] split = query.split("\\$");
        List<Predicate> results = new ArrayList<>();
        List<Predicate> results2 = new ArrayList<>();
        List<Predicate> results3 = new ArrayList<>();
        for (String str : split) {
            if (str.contains("<=")) {
                int i = Integer.parseInt(str.replace("<=", ""));
                results.add(cb.lessThanOrEqualTo(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                results2.add(cb.lessThanOrEqualTo(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                results3.add(cb.lessThanOrEqualTo(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
            } else if (str.contains(">=")) {
                int i = Integer.parseInt(str.replace(">=", ""));
                results.add(cb.greaterThanOrEqualTo(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                results2.add(cb.greaterThanOrEqualTo(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                results3.add(cb.greaterThanOrEqualTo(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
            } else if (str.contains("<")) {
                int i = Integer.parseInt(str.replace("<", ""));
                results.add(cb.lessThan(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                results2.add(cb.lessThan(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                results3.add(cb.lessThan(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
            } else if (str.contains(">")) {
                int i = Integer.parseInt(str.replace(">", ""));
                results.add(cb.greaterThan(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                results2.add(cb.greaterThan(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                results3.add(cb.greaterThan(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
            } else {
                throw new UnsupportedQueryException();
            }
        }
        Predicate and = cb.and(results.toArray(new Predicate[0]));
        Predicate and2 = cb.and(results2.toArray(new Predicate[0]));
        Predicate and3 = cb.and(results3.toArray(new Predicate[0]));
        return cb.or(and, and2, and3);
    }
}
