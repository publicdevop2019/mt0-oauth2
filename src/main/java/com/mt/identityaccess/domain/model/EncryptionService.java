package com.mt.identityaccess.domain.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptionService {
    @Autowired
    BCryptPasswordEncoder encoder;
    public String encryptedValue(String secret) {
        return encoder.encode(secret);
    }
}
