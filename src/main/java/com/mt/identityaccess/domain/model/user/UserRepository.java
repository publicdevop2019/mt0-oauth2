package com.mt.identityaccess.domain.model.user;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.user.UserQuery;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserId nextIdentity();

    Optional<User> userOfId(UserId userId);

    void add(User user);

    Optional<User> searchExistingUserWith(UserEmail email);

    SumPagedRep<User> usersOfQuery(UserQuery userQuery, DefaultPaging userPaging, QueryConfig queryConfig);

    void remove(User user1);

    void batchLock(List<PatchCommand> commands);
}