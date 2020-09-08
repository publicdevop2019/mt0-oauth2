package com.hw.aggregate.user;

import com.hw.config.CommonTokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RevokeBizUserTokenService extends CommonTokenRevocationService {


    @Value("${url.zuul.revoke-tokens}")
    private String url;


    public void blacklist(Long id) {
        blacklist(url, id, TokenTypeEnum.USER);
    }


}
