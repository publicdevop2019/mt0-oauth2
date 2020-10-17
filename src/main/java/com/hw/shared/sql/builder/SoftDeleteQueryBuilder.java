package com.hw.shared.sql.builder;

import com.hw.shared.AuditorAwareImpl;
import com.hw.shared.sql.clause.SelectFieldIdWhereClause;
import com.hw.shared.sql.clause.WhereClause;
import com.hw.shared.sql.exception.EmptyQueryValueException;
import com.hw.shared.sql.exception.EmptyWhereClauseException;
import com.hw.shared.sql.exception.UnknownWhereClauseException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;
import static com.hw.shared.Auditable.*;

public abstract class SoftDeleteQueryBuilder<T> {
    @Autowired
    protected EntityManager em;
    protected Set<WhereClause<T>> defaultWhereField = new HashSet<>();
    protected boolean allowEmptyClause = false;
    protected Map<String, WhereClause<T>> supportedWhereField = new HashMap<>();
    protected SoftDeleteQueryBuilder(){
        supportedWhereField.put(COMMON_ENTITY_ID, new SelectFieldIdWhereClause<>());
    }

    public Integer delete(String search, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(clazz);
        Root<T> root = criteriaUpdate.from(clazz);
        Predicate and = getPredicate(search, cb, root);
        criteriaUpdate.where(and);
        criteriaUpdate.set(ENTITY_DELETED, true);
        Optional<String> currentAuditor = AuditorAwareImpl.getAuditor();
        criteriaUpdate.set(ENTITY_DELETED_BY, currentAuditor.orElse(""));
        criteriaUpdate.set(ENTITY_DELETED_AT, new Date());
        return em.createQuery(criteriaUpdate).executeUpdate();
    }

    private Predicate getPredicate(String search, CriteriaBuilder cb, Root<T> root) {
        List<Predicate> results = new ArrayList<>();
        if (search == null) {
            if (!allowEmptyClause)
                throw new EmptyWhereClauseException();
        } else {
            String[] queryParams = search.split(",");
            for (String param : queryParams) {
                String[] split = param.split(":");
                if (split.length == 2) {
                    if (supportedWhereField.get(split[0]) == null)
                        throw new UnknownWhereClauseException();
                    if (supportedWhereField.get(split[0]) != null && !split[1].isBlank()) {
                        WhereClause<T> tWhereClause = supportedWhereField.get(split[0]);
                        Predicate whereClause = tWhereClause.getWhereClause(split[1], cb, root);
                        results.add(whereClause);
                    }
                } else {
                    throw new EmptyQueryValueException();
                }
            }
        }
        if (defaultWhereField.size() != 0) {
            Set<Predicate> collect = defaultWhereField.stream().map(e -> e.getWhereClause(null, cb, root)).collect(Collectors.toSet());
            results.addAll(collect);
        }
        return cb.and(results.toArray(new Predicate[0]));
    }
}
