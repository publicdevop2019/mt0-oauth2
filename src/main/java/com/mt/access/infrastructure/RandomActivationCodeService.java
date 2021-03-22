package com.mt.access.infrastructure;

import com.mt.access.domain.service.ActivationCodeService;
import org.springframework.stereotype.Service;

@Service
public class RandomActivationCodeService implements ActivationCodeService {
    @Override
    public String generate() {
        return "123456";// for testing
//        int m = (int) Math.pow(10, 6 - 1);
//        return String.valueOf(m + new Random().nextInt(9 * m));
    }
}
