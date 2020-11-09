package com.hw.shared.sql.builder;

import com.hw.shared.sql.clause.WhereClause;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class HardDeleteQueryBuilder<T> {
    @Autowired
    protected EntityManager em;
    protected Set<WhereClause<T>> defaultWhereField = new HashSet<>();
    protected boolean allowEmptyClause = false;
    protected abstract Predicate getWhereClause(Root<T> root, String fieldName);

    public Integer delete(String search, Class<T> clazz) {
        List<Predicate> results = new ArrayList<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<T> criteriaDeleteSku = cb.createCriteriaDelete(clazz);
        Root<T> root = criteriaDeleteSku.from(clazz);
        Predicate predicate = getWhereClause(root, search);
        if (!defaultWhereField.isEmpty()) {
            Set<Predicate> collect = defaultWhereField.stream().map(e -> e.getWhereClause(null, cb, root,null)).collect(Collectors.toSet());
            results.addAll(collect);
        }
        if (predicate != null)
            results.add(predicate);
        Predicate and = cb.and(results.toArray(new Predicate[0]));
        criteriaDeleteSku.where(and);
        return em.createQuery(criteriaDeleteSku).executeUpdate();
    }
}
