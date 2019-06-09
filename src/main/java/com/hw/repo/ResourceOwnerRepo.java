package com.hw.repo;

import com.hw.entity.ResourceOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceOwnerRepo extends JpaRepository<ResourceOwner, Long> {
    ResourceOwner findOneByEmail(String email);
}