package com.hw.aggregate.pending_user;

import com.hw.aggregate.pending_user.model.PendingBizUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingBizUserRepo extends JpaRepository<PendingBizUser, Long> {

    PendingBizUser findOneByEmail(String email);
}
