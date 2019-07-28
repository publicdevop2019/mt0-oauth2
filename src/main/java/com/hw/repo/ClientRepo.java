package com.hw.repo;

import com.hw.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client, Long> {
    Client findByClientId(String clientId);
}
