package com.hw.aggregate.client;

import com.hw.aggregate.client.model.BizClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BizClientRepo extends JpaRepository<BizClient, Long> {
    Optional<BizClient> findByClientId(String clientId);
}
