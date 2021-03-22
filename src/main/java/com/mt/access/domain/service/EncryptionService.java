package com.mt.access.domain.service;

import com.mt.access.domain.model.user.CurrentPassword;
import com.mt.access.domain.model.user.UserPassword;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface EncryptionService {
    String encryptedValue(String secret);

    PasswordEncoder getEncoder();

    boolean compare(UserPassword userPassword, CurrentPassword currentPwd);
}
