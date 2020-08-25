package com.hw.aggregate.user;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.authorize_code.model.TokenRevocationService;
import com.hw.aggregate.token.model.CommonTokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ProxyBizUserTokenRevocationService extends CommonTokenRevocationService implements TokenRevocationService<BizUser> {


    @Value("${url.zuul.resourceOwner}")
    private String url;

    /**
     * aspects: authority, lock
     * unlock a user should not revoke
     *
     * @param oldResourceOwner
     * @param newResourceOwner
     * @return
     */
    @Override
    public boolean shouldRevoke(BizUser oldResourceOwner, BizUser newResourceOwner) {
        if (authorityChanged(oldResourceOwner, newResourceOwner)) {
            return true;
        } else return lockUser(newResourceOwner);
    }

    @Override
    public void blacklist(String name, boolean shouldRevoke) {
        blacklist(url, name, shouldRevoke);
    }

    private boolean lockUser(BizUser newResourceOwner) {
        return Boolean.TRUE.equals(newResourceOwner.getLocked());
    }

    private boolean authorityChanged(BizUser oldResourceOwner, BizUser newResourceOwner) {
        HashSet<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities = new HashSet<>(oldResourceOwner.getGrantedAuthorities());
        HashSet<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities1 = new HashSet<>(newResourceOwner.getGrantedAuthorities());
        return !grantedAuthorities.equals(grantedAuthorities1);
    }

}
