package com.hw.aggregate.pending_user;

import com.hw.aggregate.pending_user.model.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingUserRepo extends JpaRepository<PendingUser, Long> {

    PendingUser findOneByEmail(String email);
}
