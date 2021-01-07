package com.mt.identityaccess.domain.model.user;

import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.persistence.QueryConfig;
import com.mt.identityaccess.application.user.UserPaging;
import com.mt.identityaccess.application.user.UserQuery;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserId nextIdentity();

    Optional<User> userOfId(UserId userId);

    void add(User user);

    Optional<User> searchExistingUserWith(String email);

    SumPagedRep<User> usersOfQuery(UserQuery userQuery, UserPaging userPaging, QueryConfig queryConfig);

    void remove(User user1);

    void batchLock(List<PatchCommand> commands);
}