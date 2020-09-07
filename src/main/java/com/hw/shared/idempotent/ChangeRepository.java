package com.hw.shared.idempotent;

import com.hw.shared.idempotent.model.ChangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangeRepository extends JpaRepository<ChangeRecord, Long> {
    Optional<ChangeRecord> findByChangeIdAndEntityType(String changeId,String entityType);
}
