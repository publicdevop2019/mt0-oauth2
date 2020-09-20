package com.hw.shared.sql.builder;

import com.hw.shared.AuditorAwareImpl;
import com.hw.shared.sql.clause.WhereClause;
import com.hw.shared.sql.exception.EmptyWhereClauseException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.shared.Auditable.*;

public abstract class SoftDeleteQueryBuilder<T> {

    protected EntityManager em;
    protected Set<WhereClause<T>> defaultWhereField = new HashSet<>();
    protected boolean allowEmptyClause = false;

    protected abstract Predicate getWhereClause(Root<T> root, String fieldName);

    public Integer delete(String query, Class<T> clazz) {
        List<Predicate> results = new ArrayList<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(clazz);
        Root<T> root = criteriaUpdate.from(clazz);
        if (query == null && !allowEmptyClause)
            throw new EmptyWhereClauseException();
        if (query != null) {
            Predicate predicate = getWhereClause(root, query);
            results.add(predicate);
        }
        if (!defaultWhereField.isEmpty()) {
            Set<Predicate> collect = defaultWhereField.stream().map(e -> e.getWhereClause(null, cb, root)).collect(Collectors.toSet());
            results.addAll(collect);
        }
        Predicate and = cb.and(results.toArray(new Predicate[0]));
        criteriaUpdate.where(and);
        criteriaUpdate.set(ENTITY_DELETED, true);
        Optional<String> currentAuditor = AuditorAwareImpl.getAuditor();
        criteriaUpdate.set(ENTITY_DELETED_BY, currentAuditor.orElse(""));
        criteriaUpdate.set(ENTITY_DELETED_AT, new Date());
        return em.createQuery(criteriaUpdate).executeUpdate();
    }
}
