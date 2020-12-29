package com.mt.identityaccess.domain.model.user;

import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.QueryConfig;
import com.mt.identityaccess.application.user.UserPaging;
import com.mt.identityaccess.application.user.UserQuery;

import java.util.Optional;

public interface UserRepository {
    UserId nextIdentity();

    Optional<User> userOfId(UserId userId);

    void add(User user);

    Optional<User> searchExistingUserWith(String email);

    SumPagedRep<User> usersOfQuery(UserQuery userQuery, UserPaging userPaging, QueryConfig queryConfig);

    void remove(User user1);
}