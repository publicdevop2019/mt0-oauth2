package com.hw.aggregate.user;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import com.hw.config.CommonTokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class RevokeBizUserTokenService extends CommonTokenRevocationService {


    @Value("${url.zuul.resourceOwner}")
    private String url;


    public void blacklist(String name) {
        blacklist(url, name);
    }


}
