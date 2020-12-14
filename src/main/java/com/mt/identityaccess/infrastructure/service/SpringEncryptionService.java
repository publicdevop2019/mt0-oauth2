package com.mt.identityaccess.infrastructure.service;

import com.mt.identityaccess.domain.model.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SpringEncryptionService implements EncryptionService {
    @Autowired
    BCryptPasswordEncoder encoder;

    public String encryptedValue(String secret) {
        return encoder.encode(secret);
    }
}
