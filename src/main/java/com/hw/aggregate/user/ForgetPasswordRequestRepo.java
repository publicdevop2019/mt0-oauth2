package com.hw.aggregate.user;

import com.hw.aggregate.user.model.ForgetPasswordRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgetPasswordRequestRepo extends JpaRepository<ForgetPasswordRequest, Long> {
    ForgetPasswordRequest findOneByEmail(String email);
}
