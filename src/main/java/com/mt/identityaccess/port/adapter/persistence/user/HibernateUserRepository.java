package com.mt.identityaccess.port.adapter.persistence.user;

import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.QueryConfig;
import com.mt.identityaccess.application.user.UserPaging;
import com.mt.identityaccess.application.user.UserQuery;
import com.mt.identityaccess.domain.model.user.User;
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
public interface HibernateUserRepository extends JpaRepository<User, Long>, UserRepository {
    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id=?1")
    void softDelete(Long id);

    Optional<User> findByUserIdAndDeletedFalse(UserId userId);

    Optional<User> findByEmailEmail(String email);

    default UserId nextIdentity() {
        return new UserId();
    }

    default Optional<User> userOfId(UserId userId) {
        return findByUserIdAndDeletedFalse(userId);
    }

    default void add(User user) {
        save(user);
    }

    default Optional<User> searchExistingUserWith(String email) {
        return findByEmailEmail(email);
    }

    default void remove(User user) {
        softDelete(user.getId());
    }

    default SumPagedRep<User> usersOfQuery(UserQuery userQuery, UserPaging userPaging, QueryConfig queryConfig) {
        return getSumPagedRep(userQuery.getValue(), userPaging.value(), queryConfig.value());
    }

    default void batchLock(List<PatchCommand> commands) {
        QueryBuilderRegistry.updateUserQueryBuilder().update(commands, User.class);
    }

    default SumPagedRep<User> usersOfQuery(UserQuery userQuery, UserPaging userPaging) {
        return getSumPagedRep(userQuery.getValue(), userPaging.value(), null);
    }

    private SumPagedRep<User> getSumPagedRep(String query, String page, String config) {
        UserQueryBuilder userQueryBuilder = QueryBuilderRegistry.userQueryBuilder();
        List<User> select = userQueryBuilder.select(query, page, User.class);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = userQueryBuilder.selectCount(query, User.class);
        }
        return new SumPagedRep<>(select, aLong);
    }

    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }
}
