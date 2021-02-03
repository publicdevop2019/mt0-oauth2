package com.mt.identityaccess.infrastructure;

import com.mt.identityaccess.domain.model.user.CurrentPassword;
import com.mt.identityaccess.domain.model.user.UserPassword;
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

    @Override
    public boolean compare(UserPassword userPassword, CurrentPassword currentPwd) {
        return encoder.matches(currentPwd.getRawPassword(), userPassword.getPassword());
    }

    public String encryptedValue(String secret) {
        return encoder.encode(secret);
    }

}
