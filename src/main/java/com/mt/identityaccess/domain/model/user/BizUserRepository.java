package com.mt.identityaccess.domain.model.user;

import com.mt.identityaccess.domain.model.user.BizUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizUserRepository extends JpaRepository<BizUser, Long> {
}