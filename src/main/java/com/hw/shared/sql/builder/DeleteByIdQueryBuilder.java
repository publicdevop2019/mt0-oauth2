package com.hw.shared.sql.builder;

import com.hw.shared.sql.exception.EmptyWhereClauseException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

public abstract class DeleteByIdQueryBuilder<T> extends SoftDeleteQueryBuilder<T> {
    protected String inputFieldLiteral = COMMON_ENTITY_ID;
    protected String mappedSqlFieldLiteral = COMMON_ENTITY_ID;

    @Override
    public Predicate getWhereClause(Root<T> root, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (search == null)
            throw new EmptyWhereClauseException();
        String[] queryParams = search.split(",");
        List<Predicate> results = new ArrayList<>();
        for (String param : queryParams) {
            String[] split = param.split(":");
            if (split.length == 2) {
                if (inputFieldLiteral.equals(split[0]) && !split[1].isBlank()) {
                    results.add(getIdWhereClause(split[1], cb, root));
                }
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    private Predicate getIdWhereClause(String s, CriteriaBuilder cb, Root<T> root) {
        String[] split = s.split("\\.");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            results.add(cb.equal(root.get(mappedSqlFieldLiteral), Long.parseLong(str)));
        }
        return cb.or(results.toArray(new Predicate[0]));
    }
}
