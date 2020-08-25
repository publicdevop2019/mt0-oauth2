package com.hw.aggregate.user;

import com.hw.aggregate.user.model.PendingBizUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingBizUserRepo extends JpaRepository<PendingBizUser, Long> {

    PendingBizUser findOneByEmail(String email);
}
