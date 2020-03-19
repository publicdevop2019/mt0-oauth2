package com.hw.repo;

import com.hw.entity.PendingResourceOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingResourceOwnerRepo extends JpaRepository<PendingResourceOwner, Long> {

    PendingResourceOwner findOneByEmail(String email);
}
