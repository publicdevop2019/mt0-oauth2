package com.mt.identityaccess.domain.model.user;

import java.util.Optional;

public interface UserRepository {
    UserId nextIdentity();

    Optional<User> userOfId(UserId userId);

    void add(User user);

    Optional<User> searchExistingUserWith(String email);
}