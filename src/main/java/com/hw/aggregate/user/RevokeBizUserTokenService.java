package com.hw.aggregate.user;

import com.hw.config.CommonTokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RevokeBizUserTokenService extends CommonTokenRevocationService {


    @Value("${url.zuul.resourceOwner}")
    private String url;


    public void blacklist(String name) {
        blacklist(url, name);
    }


}
