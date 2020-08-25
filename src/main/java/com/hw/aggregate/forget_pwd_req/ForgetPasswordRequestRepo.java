package com.hw.aggregate.forget_pwd_req;

import com.hw.aggregate.forget_pwd_req.model.ForgetPasswordRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgetPasswordRequestRepo extends JpaRepository<ForgetPasswordRequest, Long> {
    ForgetPasswordRequest findOneByEmail(String email);
}
