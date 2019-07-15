package com.hw.unit.service;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.service.ResourceOwnerTokenRevocationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

public class ResourceOwnerTokenRevocationServiceTest {
    private ResourceOwnerTokenRevocationService resourceOwnerTokenRevocationService = new ResourceOwnerTokenRevocationService();

    @Before
    public void init(){
        ReflectionTestUtils.setField(resourceOwnerTokenRevocationService,"enabled",true);
    }

    @Test
    public void happy_shouldRevoke_lock() {
        ResourceOwner resourceOwner = getResourceOwner();
        ResourceOwner resourceOwner2 = getResourceOwner();
        resourceOwner2.setLocked(Boolean.TRUE);
        boolean b = resourceOwnerTokenRevocationService.shouldRevoke(resourceOwner, resourceOwner2);
        Assert.assertEquals(true, b);
    }

    @Test
    public void sad_shouldRevoke_unlock() {
        ResourceOwner resourceOwner = getResourceOwner();
        resourceOwner.setLocked(Boolean.TRUE);
        ResourceOwner resourceOwner2 = getResourceOwner();
        resourceOwner2.setLocked(Boolean.FALSE);
        boolean b = resourceOwnerTokenRevocationService.shouldRevoke(resourceOwner, resourceOwner2);
        Assert.assertEquals(false, b);
    }

    @Test
    public void sad_shouldRevoke_authority_same() {
        ResourceOwner resourceOwner = getResourceOwner();
        ResourceOwner resourceOwner2 = getResourceOwner();
        boolean b = resourceOwnerTokenRevocationService.shouldRevoke(resourceOwner, resourceOwner2);
        Assert.assertEquals(false, b);
    }

    @Test
    public void sad_shouldRevoke_authority_diff() {
        ResourceOwner resourceOwner = getResourceOwner();
        ResourceOwner resourceOwner2 = getResourceOwner();
        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(ResourceOwnerAuthorityEnum.class, ResourceOwnerAuthorityEnum.ROLE_USER.toString());
        GrantedAuthorityImpl grantedAuthority2 = GrantedAuthorityImpl.getGrantedAuthority(ResourceOwnerAuthorityEnum.class, ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString());
        resourceOwner2.setGrantedAuthorities(Arrays.asList(grantedAuthority, grantedAuthority2));
        boolean b = resourceOwnerTokenRevocationService.shouldRevoke(resourceOwner, resourceOwner2);
        Assert.assertEquals(true, b);
    }
    @Test
    public void sad_shouldRevoke_authority_diff_disabled() {
        ReflectionTestUtils.setField(resourceOwnerTokenRevocationService,"enabled",false);
        ResourceOwner resourceOwner = getResourceOwner();
        ResourceOwner resourceOwner2 = getResourceOwner();
        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(ResourceOwnerAuthorityEnum.class, ResourceOwnerAuthorityEnum.ROLE_USER.toString());
        GrantedAuthorityImpl grantedAuthority2 = GrantedAuthorityImpl.getGrantedAuthority(ResourceOwnerAuthorityEnum.class, ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString());
        resourceOwner2.setGrantedAuthorities(Arrays.asList(grantedAuthority, grantedAuthority2));
        boolean b = resourceOwnerTokenRevocationService.shouldRevoke(resourceOwner, resourceOwner2);
        Assert.assertEquals(false, b);
    }

    private ResourceOwner getResourceOwner() {
        ResourceOwner resourceOwner = new ResourceOwner();
        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(ResourceOwnerAuthorityEnum.class, ResourceOwnerAuthorityEnum.ROLE_USER.toString());
        resourceOwner.setGrantedAuthorities(Arrays.asList(grantedAuthority));
        resourceOwner.setLocked(Boolean.FALSE);
        return resourceOwner;
    }
}