package com.mt.identityaccess.domain.service;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface EncryptionService {
    String encryptedValue(String secret);

    boolean compare(String encrypted, String raw);

    PasswordEncoder getEncoder();
}
