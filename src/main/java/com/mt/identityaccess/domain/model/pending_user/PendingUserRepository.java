package com.mt.identityaccess.domain.model.pending_user;

import java.util.Optional;

public interface PendingUserRepository {
    Optional<PendingUser> registeredUsing(String email);
}
