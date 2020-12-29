package com.mt.identityaccess.infrastructure;

import com.mt.identityaccess.domain.service.PasswordResetTokenService;
import org.springframework.stereotype.Service;

@Service
public class RandomPasswordResetTokenService implements PasswordResetTokenService {
    @Override
    public String generate() {
        return "123456789";
//        return UUID.randomUUID().toString().replace("-", "");
    }
}
