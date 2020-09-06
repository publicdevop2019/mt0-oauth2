package com.hw.shared.sql.builder;

import com.hw.shared.AuditorAwareImpl;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.Optional;

import static com.hw.shared.Auditable.*;

public abstract class SoftDeleteQueryBuilder<T> {

    protected EntityManager em;

    protected abstract Predicate getWhereClause(Root<T> root, String fieldName);

    public Integer delete(String search, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(clazz);
        Root<T> root = criteriaUpdate.from(clazz);
        Predicate predicate = getWhereClause(root, search);
        if (predicate != null)
            criteriaUpdate.where(predicate);
        criteriaUpdate.set(ENTITY_DELETED, true);
        Optional<String> currentAuditor = AuditorAwareImpl.getAuditor();
        criteriaUpdate.set(ENTITY_DELETED_BY, currentAuditor.orElse(""));
        criteriaUpdate.set(ENTITY_DELETED_AT, new Date());
        return em.createQuery(criteriaUpdate).executeUpdate();
    }
}
