package com.mt.identityaccess.domain.model.pending_user;

import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {

    PendingUser findOneByEmail(String email);
}
