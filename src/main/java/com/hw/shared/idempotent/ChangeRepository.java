package com.hw.shared.idempotent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangeRepository extends JpaRepository<ChangeRecord, Long> {
    Optional<ChangeRecord> findByChangeId(String changeId);
}
