package com.mt.identityaccess.domain.model.user;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserId nextIdentity();

    Optional<User> userOfId(UserId userId);

    void add(User user);

    Optional<User> searchExistingUserWith(UserEmail email);

    SumPagedRep<User> usersOfQuery(UserQuery userQuery, PageConfig userPaging, QueryConfig queryConfig);

    void remove(User user1);

    void batchLock(List<PatchCommand> commands);
}