package com.mt.identityaccess.infrastructure;

import com.mt.identityaccess.domain.service.EncryptionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SpringEncryptionService implements EncryptionService {
    private static final Integer STRENGTH = 12;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(STRENGTH);

    public String encryptedValue(String secret) {
        return encoder.encode(secret);
    }

    @Override
    public boolean compare(String encrypted, String raw) {
        return encoder.matches(raw, encrypted);
    }
}
