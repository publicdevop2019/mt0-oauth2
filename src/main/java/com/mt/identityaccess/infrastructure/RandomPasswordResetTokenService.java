package com.mt.identityaccess.infrastructure;

import com.mt.identityaccess.domain.service.ActivationCodeService;
import org.springframework.stereotype.Service;

@Service
public class RandomPasswordResetTokenService implements ActivationCodeService {
    @Override
    public String generate() {
        return "123456789";
//        return UUID.randomUUID().toString().replace("-", "");
    }
}
