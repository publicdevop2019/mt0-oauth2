package com.hw.repo;

import com.hw.entity.ForgetPasswordRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgetPasswordRequestRepo extends JpaRepository<ForgetPasswordRequest, Long> {
    ForgetPasswordRequest findOneByEmail(String email);
}
