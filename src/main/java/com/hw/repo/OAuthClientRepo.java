package com.hw.repo;

import com.hw.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthClientRepo extends JpaRepository<Client, Long> {
    Client findByClientId(String clientId);
}
