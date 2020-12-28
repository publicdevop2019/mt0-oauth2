package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.sql.clause.WhereClause;
import com.mt.identityaccess.domain.model.client.Client;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

public class SelectFieldGrantEnabledBooleanClause extends WhereClause<Client> {
    public SelectFieldGrantEnabledBooleanClause() {
        super();
    }

    @Override
    public Predicate getWhereClause(String query, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
        if (query.contains("$")) {
            Set<String> strings = new TreeSet<>(Arrays.asList(query.split("\\$")));
            List<Predicate> list2 = new ArrayList<>();
            for (String str : strings) {
                if ("CLIENT_CREDENTIALS".equalsIgnoreCase(str)) {
                    list2.add(cb.isTrue(root.get("clientCredentialsGrant").get("enabled")));
                } else if ("PASSWORD".equalsIgnoreCase(str)) {
                    list2.add(cb.isTrue(root.get("passwordGrant").get("enabled")));
                } else if ("AUTHORIZATION_CODE".equalsIgnoreCase(str)) {
                    list2.add(cb.isTrue(root.get("authorizationCodeGrant").get("enabled")));
                } else if ("REFRESH_TOKEN".equalsIgnoreCase(str)) {
                    list2.add(cb.isTrue(root.get("passwordGrant").get("refreshTokenGrant").get("enabled")));
                }
            }
            return cb.and(list2.toArray(Predicate[]::new));
        } else {
            return getExpression(query, cb, root);
        }
    }

    protected Predicate getExpression(String str, CriteriaBuilder cb, Root<Client> root) {
        if ("CLIENT_CREDENTIALS".equalsIgnoreCase(str)) {
            return cb.isTrue(root.get("clientCredentialsGrant").get("enabled"));
        } else if ("PASSWORD".equalsIgnoreCase(str)) {
            return cb.isTrue(root.get("passwordGrant").get("enabled"));
        } else if ("AUTHORIZATION_CODE".equalsIgnoreCase(str)) {
            return cb.isTrue(root.get("authorizationCodeGrant").get("enabled"));
        } else if ("REFRESH_TOKEN".equalsIgnoreCase(str)) {
            return cb.isTrue(root.get("passwordGrant").get("refreshTokenGrant").get("enabled"));
        } else {
            return null;
        }
    }
}
