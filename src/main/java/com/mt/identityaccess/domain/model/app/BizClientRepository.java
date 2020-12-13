package com.mt.identityaccess.domain.model.app;

import com.mt.identityaccess.domain.model.app.BizClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizClientRepository extends JpaRepository<BizClient, Long> {
}
