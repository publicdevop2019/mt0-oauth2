package com.mt.identityaccess.port.adapter.persistence.user;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.builder.SelectQueryBuilder;
import com.mt.identityaccess.application.user.UserQuery;
import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.domain.model.user.UserEmail;
import com.mt.identityaccess.domain.model.user.UserId;
import com.mt.identityaccess.domain.model.user.UserRepository;
import com.mt.identityaccess.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataJpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id=?1")
    void softDelete(Long id);

    Optional<User> findByEmailEmail(String email);

    default UserId nextIdentity() {
        return new UserId();
    }

    default Optional<User> userOfId(UserId userId) {
        return getUserOfId(userId);
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

    default SumPagedRep<User> usersOfQuery(UserQuery query, PageConfig userPaging, QueryConfig queryConfig) {
        return QueryUtility.pagedQuery(QueryBuilderRegistry.userQueryBuilder(), query, userPaging, new QueryConfig(), User.class);
    }

    default void batchLock(List<PatchCommand> commands) {
        QueryBuilderRegistry.updateUserQueryBuilder().update(commands, User.class);
    }

    private Optional<User> getUserOfId(UserId userId) {
        SelectQueryBuilder<User> userSelectQueryBuilder = QueryBuilderRegistry.userQueryBuilder();
        List<User> select = userSelectQueryBuilder.select(new UserQuery(userId), new PageConfig(), User.class);
        if (select.isEmpty())
            return Optional.empty();
        return Optional.of(select.get(0));
    }
}
