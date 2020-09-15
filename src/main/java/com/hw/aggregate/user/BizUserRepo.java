package com.hw.aggregate.user;

import com.hw.aggregate.user.model.BizUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizUserRepo extends JpaRepository<BizUser, Long> {
}