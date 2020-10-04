package com.hw.shared.sql.builder;

import com.hw.shared.Auditable;
import com.hw.shared.sql.clause.SelectFieldIdWhereClause;
import com.hw.shared.sql.clause.SelectNotDeletedClause;
import com.hw.shared.sql.clause.WhereClause;
import com.hw.shared.sql.exception.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

public abstract class SelectQueryBuilder<T extends Auditable> {
    protected Integer DEFAULT_PAGE_SIZE = 10;
    protected Integer MAX_PAGE_SIZE = 20;
    protected Integer DEFAULT_PAGE_NUM = 0;
    protected String DEFAULT_SORT_BY = COMMON_ENTITY_ID;
    protected Map<String, String> mappedSortBy = new HashMap<>();
    protected Map<String, WhereClause<T>> supportedWhereField = new HashMap<>();
    protected Set<WhereClause<T>> defaultWhereField = new HashSet<>();
    protected Sort.Direction DEFAULT_SORT_ORDER = Sort.Direction.ASC;
    protected EntityManager em;
    protected boolean allowEmptyClause = false;

    protected SelectQueryBuilder() {
        mappedSortBy.put(COMMON_ENTITY_ID, COMMON_ENTITY_ID);
        supportedWhereField.put(COMMON_ENTITY_ID, new SelectFieldIdWhereClause<>());
    }

    public List<T> select(String search, String page, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        PageRequest pageRequest = getPageRequest(page);
        Predicate and = getPredicate(search, cb, root);
        if (and != null)
            query.where(and);
        Set<Order> collect = pageRequest.getSort().get().map(e -> {
            if (e.getDirection().isAscending()) {
                return cb.asc(root.get(e.getProperty()));
            } else {
                return cb.desc(root.get(e.getProperty()));
            }
        }).collect(Collectors.toSet());
        query.orderBy(collect.toArray(Order[]::new));

        TypedQuery<T> query1 = em.createQuery(query)
                .setFirstResult(BigDecimal.valueOf(pageRequest.getOffset()).intValue())
                .setMaxResults(pageRequest.getPageSize());
        return query1.getResultList();
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
        //force to select only not deleted entity
        Predicate notSoftDeleted = new SelectNotDeletedClause<T>().getWhereClause(cb, root);
        results.add(notSoftDeleted);
        return cb.and(results.toArray(new Predicate[0]));
    }

    public Long selectCount(String search, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(clazz);
        query.select(cb.count(root));
        Predicate and = getPredicate(search, cb, root);
        if (and != null)
            query.where(and);
        return em.createQuery(query).getSingleResult();
    }

    private PageRequest getPageRequest(String page) {
        if (page == null) {
            Sort sort = new Sort(DEFAULT_SORT_ORDER, mappedSortBy.get(DEFAULT_SORT_BY));
            return PageRequest.of(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE, sort);
        }
        String[] params = page.split(",");
        Integer pageNumber = DEFAULT_PAGE_NUM;
        Integer pageSize = DEFAULT_PAGE_SIZE;
        String sortBy = mappedSortBy.get(DEFAULT_SORT_BY);
        Sort.Direction sortOrder = DEFAULT_SORT_ORDER;
        for (String param : params) {
            String[] values = param.split(":");
            if (values[0].equals("num") && values[1] != null) {
                pageNumber = Integer.parseInt(values[1]);
            }
            if (values[0].equals("size") && values[1] != null) {
                pageSize = Integer.parseInt(values[1]);
            }
            if (values[0].equals("by") && values[1] != null) {
                sortBy = mappedSortBy.get(values[1]);
                if (sortBy == null)
                    throw new UnsupportedQueryException();
            }
            if (values[0].equals("order") && values[1] != null) {
                sortOrder = Sort.Direction.fromString(values[1]);
            }
        }
        if (pageSize > MAX_PAGE_SIZE)
            throw new MaxPageSizeExceedException();
        Sort sort = new Sort(sortOrder, sortBy);
        return PageRequest.of(pageNumber, pageSize, sort);
    }

}
