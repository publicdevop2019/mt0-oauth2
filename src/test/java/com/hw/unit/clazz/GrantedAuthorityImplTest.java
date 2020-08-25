package com.hw.unit.clazz;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import org.junit.Assert;
import org.junit.Test;

public class GrantedAuthorityImplTest {
    @Test
    public void getGrantedAuthority() {
        Assert.assertNotNull(GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_BACKEND.toString()));
    }

    @Test
    public void getAuthority() {
        GrantedAuthorityImpl grantedAuthority = new GrantedAuthorityImpl(BizClientAuthorityEnum.ROLE_BACKEND);
        String authority = grantedAuthority.getGrantedAuthority().toString();
        Assert.assertEquals(BizClientAuthorityEnum.ROLE_BACKEND.toString(), authority);
    }

    @Test
    public void setAuthority() {
        GrantedAuthorityImpl grantedAuthority = new GrantedAuthorityImpl();
        grantedAuthority.setGrantedAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
        String authority = grantedAuthority.getGrantedAuthority().toString();
        Assert.assertEquals(BizClientAuthorityEnum.ROLE_BACKEND.toString(), authority);

    }

    @Test
    public void setAuthority_overwrite() {
        GrantedAuthorityImpl grantedAuthority = new GrantedAuthorityImpl(BizClientAuthorityEnum.ROLE_FIRST_PARTY);
        grantedAuthority.setGrantedAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
        String authority = grantedAuthority.getGrantedAuthority().toString();
        Assert.assertEquals(BizClientAuthorityEnum.ROLE_BACKEND.toString(), authority);

    }

    @Test
    public void happy_equals1() {
        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_BACKEND.toString());
        GrantedAuthorityImpl grantedAuthority2 = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_BACKEND.toString());
        Assert.assertEquals(true, grantedAuthority.equals(grantedAuthority2));
    }

    @Test
    public void happy_hashCode1() {
        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_BACKEND.toString());
        GrantedAuthorityImpl grantedAuthority2 = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_BACKEND.toString());
        Assert.assertEquals(true, grantedAuthority.hashCode() == grantedAuthority2.hashCode());
    }

    @Test
    public void sad_equals1() {
        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_BACKEND.toString());
        GrantedAuthorityImpl grantedAuthority2 = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_THIRD_PARTY.toString());
        Assert.assertEquals(false, grantedAuthority.equals(grantedAuthority2));
    }

    @Test
    public void sad_hashCode1() {
        GrantedAuthorityImpl grantedAuthority = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_BACKEND.toString());
        GrantedAuthorityImpl grantedAuthority2 = GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, BizClientAuthorityEnum.ROLE_THIRD_PARTY.toString());
        Assert.assertEquals(false, grantedAuthority.hashCode() == grantedAuthority2.hashCode());
    }
}