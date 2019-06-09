package com.hw.repo;

import com.hw.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthClientRepo extends JpaRepository<OAuthClient, Long> {
    OAuthClient findByClientId(String clientId);
}
