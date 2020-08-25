package com.hw.aggregate.client;

import com.hw.config.CommonTokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RevokeBizClientTokenService extends CommonTokenRevocationService {

    @Value("${url.zuul.client}")
    private String url;

    public void blacklist(String name) {
        blacklist(url, name);
    }


}
