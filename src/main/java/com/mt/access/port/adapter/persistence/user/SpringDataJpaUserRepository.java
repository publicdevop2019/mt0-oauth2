package com.mt.access.port.adapter.persistence.user;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.access.domain.model.user.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.access.domain.model.user.User.*;

@Repository
public interface SpringDataJpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id=?1")
    void softDelete(Long id);

    Optional<User> findByEmailEmail(String email);

    default Optional<User> userOfId(UserId userId) {
        return usersOfQuery(new UserQuery(userId)).findFirst();
    }

    default void add(User user) {
        save(user);
    }

    default Optional<User> searchExistingUserWith(UserEmail email) {
        return findByEmailEmail(email.getEmail());
    }

    default void remove(User user) {
        softDelete(user.getId());
    }

    default SumPagedRep<User> usersOfQuery(UserQuery query) {
        return QueryBuilderRegistry.getUserQueryBuilder().execute(query);
    }

    default void batchLock(List<PatchCommand> commands) {
        QueryBuilderRegistry.getUpdateUserQueryBuilder().update(commands, User.class);
    }

    @Component
    class JpaCriteriaApiUserAdaptor {
        private static final String USER_ID_LITERAL = "userId";

        public SumPagedRep<User> execute(UserQuery userQuery) {
            QueryUtility.QueryContext<User> queryContext = QueryUtility.prepareContext(User.class, userQuery);
            Optional.ofNullable(userQuery.getUserEmails()).ifPresent(e -> {
                queryContext.getPredicates().add(UserEmailPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                queryContext.getCountPredicates().add(UserEmailPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot()));
            });

            Optional.ofNullable(userQuery.getUserIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), USER_ID_LITERAL, queryContext));
            Optional.ofNullable(userQuery.getSubscription()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, ENTITY_SUBSCRIPTION, queryContext));
            Optional.ofNullable(userQuery.getAuthoritiesSearch()).ifPresent(e -> QueryUtility.addStringLikePredicate(e, ENTITY_GRANTED_AUTHORITIES, queryContext));
            Order order = null;
            if (userQuery.getUserSort().isById())
                order = QueryUtility.getDomainIdOrder(USER_ID_LITERAL, queryContext, userQuery.getUserSort().isAsc());
            if (userQuery.getUserSort().isByEmail())
                order = QueryUtility.getOrder(ENTITY_EMAIL, queryContext, userQuery.getUserSort().isAsc());
            if (userQuery.getUserSort().isByCreateAt())
                order = QueryUtility.getOrder("createdAt", queryContext, userQuery.getUserSort().isAsc());
            if (userQuery.getUserSort().isByLocked())
                order = QueryUtility.getOrder(ENTITY_LOCKED, queryContext, userQuery.getUserSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(userQuery, queryContext);
        }

        public static class UserEmailPredicateConverter {
            public static Predicate getPredicate(Set<String> values, CriteriaBuilder cb, Root<User> root) {
                List<Predicate> results = new ArrayList<>();
                for (String str : values) {
                    results.add(cb.like(root.get("email").get("email"), "%" + str + "%"));
                }
                return cb.or(results.toArray(new Predicate[0]));
            }
        }
    }
}
