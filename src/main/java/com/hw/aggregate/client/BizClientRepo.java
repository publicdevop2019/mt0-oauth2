package com.hw.aggregate.client;

import com.hw.aggregate.client.model.BizClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BizClientRepo extends JpaRepository<BizClient, Long> {
}
