package com.mt.identityaccess.infrastructure;

import com.mt.identityaccess.domain.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SpringEncryptionService implements EncryptionService {
    private static final Integer STRENGTH = 12;
    @Autowired
    private PasswordEncoder encoder;

    @Bean//required
    @Override
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }

    public String encryptedValue(String secret) {
        return encoder.encode(secret);
    }

    @Override
    public boolean compare(String encrypted, String raw) {
        return encoder.matches(raw, encrypted);
    }
}
