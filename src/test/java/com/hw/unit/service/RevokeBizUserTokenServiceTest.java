package com.hw.unit.service;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.RevokeBizUserTokenService;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class RevokeBizUserTokenServiceTest {
    private RevokeBizUserTokenService resourceOwnerTokenRevocationServiceImpl = new RevokeBizUserTokenService();

//    @Test
//    public void happy_shouldRevoke_lock() {
//        BizUser resourceOwner = getResourceOwner();
//        BizUser resourceOwner2 = getResourceOwner();
//        resourceOwner2.setLocked(Boolean.TRUE);
//        boolean b = resourceOwnerTokenRevocationServiceImpl.shouldRevoke(resourceOwner, resourceOwner2);
//        Assert.assertEquals(true, b);
//    }
//
//    @Test
//    public void sad_shouldRevoke_unlock() {
//        BizUser resourceOwner = getResourceOwner();
//        resourceOwner.setLocked(Boolean.TRUE);
//        BizUser resourceOwner2 = getResourceOwner();
//        resourceOwner2.setLocked(Boolean.FALSE);
//        boolean b = resourceOwnerTokenRevocationServiceImpl.shouldRevoke(resourceOwner, resourceOwner2);
//        Assert.assertEquals(false, b);
//    }
//
//    @Test
//    public void sad_shouldRevoke_authority_same() {
//        BizUser resourceOwner = getResourceOwner();
//        BizUser resourceOwner2 = getResourceOwner();
//        boolean b = resourceOwnerTokenRevocationServiceImpl.shouldRevoke(resourceOwner, resourceOwner2);
//        Assert.assertEquals(false, b);
//    }
//
//    @Test
//    public void sad_shouldRevoke_authority_diff() {
//        BizUser resourceOwner = getResourceOwner();
//        BizUser resourceOwner2 = getResourceOwner();
//        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(BizUserAuthorityEnum.class, BizUserAuthorityEnum.ROLE_USER.toString());
//        GrantedAuthorityImpl grantedAuthority2 = GrantedAuthorityImpl.getGrantedAuthority(BizUserAuthorityEnum.class, BizUserAuthorityEnum.ROLE_ADMIN.toString());
//        resourceOwner2.setGrantedAuthorities(Arrays.asList(grantedAuthority, grantedAuthority2));
//        boolean b = resourceOwnerTokenRevocationServiceImpl.shouldRevoke(resourceOwner, resourceOwner2);
//        Assert.assertEquals(true, b);
//    }
//
//    private BizUser getResourceOwner() {
//        BizUser resourceOwner = new BizUser();
//        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(BizUserAuthorityEnum.class, BizUserAuthorityEnum.ROLE_USER.toString());
//        resourceOwner.setGrantedAuthorities(Arrays.asList(grantedAuthority));
//        resourceOwner.setLocked(Boolean.FALSE);
//        return resourceOwner;
//    }
}