package com.hw.service;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.interfaze.TokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ResourceOwnerTokenRevocationServiceImpl extends CommonTokenRevocationService implements TokenRevocationService<ResourceOwner> {


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
    public boolean shouldRevoke(ResourceOwner oldResourceOwner, ResourceOwner newResourceOwner) {
        if (authorityChanged(oldResourceOwner, newResourceOwner)) {
            return true;
        } else return lockUser(newResourceOwner);
    }

    @Override
    public void blacklist(String name, boolean shouldRevoke) {
        blacklist(url, name, shouldRevoke);
    }

    private boolean lockUser(ResourceOwner newResourceOwner) {
        return Boolean.TRUE.equals(newResourceOwner.getLocked());
    }

    private boolean authorityChanged(ResourceOwner oldResourceOwner, ResourceOwner newResourceOwner) {
        HashSet<GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>> grantedAuthorities = new HashSet<>(oldResourceOwner.getGrantedAuthorities());
        HashSet<GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>> grantedAuthorities1 = new HashSet<>(newResourceOwner.getGrantedAuthorities());
        return !grantedAuthorities.equals(grantedAuthorities1);
    }

}
