package com.hw.unit.service;

import com.hw.aggregate.authorize_code.model.TokenRevocationService;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.pending_user.PendingUserRepo;
import com.hw.aggregate.pending_user.model.PendingUser;
import com.hw.aggregate.user.AdminBizUserApplicationService;
import com.hw.aggregate.user.BizUserRepo;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class BizAdminBizUserApplicationServiceTest {
    private String AUTHORIZATION_HEADER_ROOT = "xx.eyJ1aWQiOiIwIiwiYXVkIjpbInByb2R1Y3QiLCJmaWxlLXVwbG9hZCIsImVkZ2UtcHJveHkiLCJ1c2VyLXByb2ZpbGUiLCJvYXV0aDItaWQiXSwidXNlcl9uYW1lIjoiaGFvbGlud2VpMjAxNUBnbWFpbC5jb20iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE1ODIyNTU2NjQsImlhdCI6MTU4MjI1NTU0NCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9ST09UIiwiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiJmOTI1YzdhOC0xMGU2LTQ1OGMtYjg1MS0zMmM3YjllMDQ2OTgiLCJjbGllbnRfaWQiOiJsb2dpbi1pZCJ9.zz";
    private String AUTHORIZATION_HEADER_ADMIN = "xx.eyJ1aWQiOiIwIiwiYXVkIjpbInByb2R1Y3QiLCJmaWxlLXVwbG9hZCIsImVkZ2UtcHJveHkiLCJ1c2VyLXByb2ZpbGUiLCJvYXV0aDItaWQiXSwidXNlcl9uYW1lIjoiaGFvbGlud2VpMjAxNUBnbWFpbC5jb20iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE1ODIyNTU2NjQsImlhdCI6MTU4MjI1NTU0NCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiJmOTI1YzdhOC0xMGU2LTQ1OGMtYjg1MS0zMmM3YjllMDQ2OTgiLCJjbGllbnRfaWQiOiJsb2dpbi1pZCJ9.zz";
    private String AUTHORIZATION_HEADER_USER = "xx.eyJ1aWQiOiIwIiwiYXVkIjpbInByb2R1Y3QiLCJmaWxlLXVwbG9hZCIsImVkZ2UtcHJveHkiLCJ1c2VyLXByb2ZpbGUiLCJvYXV0aDItaWQiXSwidXNlcl9uYW1lIjoiaGFvbGlud2VpMjAxNUBnbWFpbC5jb20iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE1ODIyNTU2NjQsImlhdCI6MTU4MjI1NTU0NCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6ImY5MjVjN2E4LTEwZTYtNDU4Yy1iODUxLTMyYzdiOWUwNDY5OCIsImNsaWVudF9pZCI6ImxvZ2luLWlkIn0=.zz";
    @InjectMocks
    AdminBizUserApplicationService resourceOwnerService = new AdminBizUserApplicationService();

    @Mock
    BizUserRepo userRepo;

    @Mock
    PendingUserRepo pendingResourceOwnerRepo;

    @Mock
    BCryptPasswordEncoder encoder;

    @Mock
    IdGenerator idGenerator;

    @Mock
    TokenRevocationService<BizUser> tokenRevocationService;

    @Test
    public void get_all_ro() {
        ArrayList<BizUser> resourceOwners1 = new ArrayList<>();
        Mockito.doReturn(resourceOwners1).when(userRepo).findAll();
        List<BizUser> resourceOwners = resourceOwnerService.readAllResourceOwners();
        Assert.assertEquals(0, resourceOwners.size());
    }

    @Test(expected = BadRequestException.class)
    public void delete_not_exist_ro() {
        Mockito.doReturn(Optional.empty()).when(userRepo).findById(any(Long.class));
        resourceOwnerService.deleteResourceOwner(new Random().nextLong());
    }

    @Test(expected = BadRequestException.class)
    public void delete_root_ro() {
        BizUser resourceOwner = getRootResourceOwner();
        Mockito.doReturn(Optional.of(resourceOwner)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.deleteResourceOwner(new Random().nextLong());
    }

    @Test(expected = BadRequestException.class)
    public void root_ro_should_not_get_update() {
        BizUser resourceOwner = getRootResourceOwner();
        Mockito.doReturn(Optional.of(resourceOwner)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(resourceOwner, new Random().nextLong(), UUID.randomUUID().toString());
    }

    @Test(expected = BadRequestException.class)
    public void no_ro_can_be_update_to_root() {
        BizUser stored = getRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(BizUserAuthorityEnum.ROLE_ADMIN);
        stored.setGrantedAuthorities(Collections.singletonList(authority));
        BizUser update = getRootResourceOwner();
        GrantedAuthorityImpl authority2 = getAuthority(BizUserAuthorityEnum.ROLE_ROOT);
        update.setGrantedAuthorities(Collections.singletonList(authority2));
        Mockito.doReturn(Optional.of(stored)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(update, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test(expected = BadRequestException.class)
    public void admin_ro_trying_to_set_ro_to_admin() {
        BizUser resourceOwner = getRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(BizUserAuthorityEnum.ROLE_ADMIN);
        resourceOwner.setGrantedAuthorities(Collections.singletonList(authority));
        Mockito.doReturn(Optional.of(resourceOwner)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(resourceOwner, new Random().nextLong(), AUTHORIZATION_HEADER_ADMIN);
    }

    @Test(expected = BadRequestException.class)
    public void admin_ro_trying_to_set_ro_subscribe_new_order() {
        BizUser updateRO = getNonRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(BizUserAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl authority2 = getAuthority(BizUserAuthorityEnum.ROLE_USER);
        updateRO.setGrantedAuthorities(List.of(authority, authority2));
        updateRO.setSubscription(Boolean.TRUE);
        Mockito.doReturn(Optional.of(updateRO)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(updateRO, new Random().nextLong(), AUTHORIZATION_HEADER_ADMIN);
    }

    @Test
    public void root_ro_trying_to_set_ro_subscribe_new_order_for_admin() {
        BizUser updateRO = getNonRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(BizUserAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl authority2 = getAuthority(BizUserAuthorityEnum.ROLE_USER);
        updateRO.setGrantedAuthorities(List.of(authority, authority2));
        updateRO.setSubscription(Boolean.TRUE);
        Mockito.doReturn(Optional.of(updateRO)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(updateRO, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test(expected = BadRequestException.class)
    public void root_ro_trying_to_set_ro_subscribe_new_order_for_user() {
        BizUser updateRO = getNonRootResourceOwner();
        GrantedAuthorityImpl authority2 = getAuthority(BizUserAuthorityEnum.ROLE_USER);
        updateRO.setGrantedAuthorities(List.of(authority2));
        updateRO.setSubscription(Boolean.TRUE);
        Mockito.doReturn(Optional.of(updateRO)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(updateRO, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test
    public void root_ro_trying_to_set_ro_to_admin() {
        BizUser stored = getRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(BizUserAuthorityEnum.ROLE_USER);
        stored.setGrantedAuthorities(Collections.singletonList(authority));
        BizUser update = getRootResourceOwner();
        GrantedAuthorityImpl authority2 = getAuthority(BizUserAuthorityEnum.ROLE_ADMIN);
        update.setGrantedAuthorities(Collections.singletonList(authority2));
        Mockito.doReturn(Optional.of(stored)).when(userRepo).findById(any(Long.class));
        Mockito.doNothing().when(tokenRevocationService).blacklist(any(String.class), any(Boolean.class));
        Mockito.doReturn(false).when(tokenRevocationService).shouldRevoke(any(BizUser.class), any(BizUser.class));
        resourceOwnerService.updateResourceOwner(update, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test
    public void create_ro() {
        BizUser create = getRootResourceOwner();
        create.setPassword(UUID.randomUUID().toString());
        PendingUser pendingResourceOwner = new PendingUser();
        pendingResourceOwner.setEmail(create.getEmail());
        pendingResourceOwner.setPassword(create.getPassword());
        pendingResourceOwner.setActivationCode(UUID.randomUUID().toString());

        Mockito.doReturn(null).when(userRepo).findOneByEmail(any(String.class));
        Mockito.doReturn(0L).when(idGenerator).getId();
        Mockito.doReturn(pendingResourceOwner).when(pendingResourceOwnerRepo).findOneByEmail(any(String.class));
        Mockito.doReturn(create).when(userRepo).save(any(BizUser.class));
        Mockito.doReturn(UUID.randomUUID().toString()).when(encoder).encode(any(String.class));

        BizUser user = resourceOwnerService.createResourceOwner(pendingResourceOwner);
        Assert.assertEquals(create.getEmail(), user.getEmail());
    }

    @Test(expected = BadRequestException.class)
    public void create_ro_with_invalid_payload() {
        BizUser create = getRootResourceOwner();
        create.setPassword(UUID.randomUUID().toString());
        PendingUser pendingResourceOwner = new PendingUser();
        pendingResourceOwner.setEmail(create.getEmail());

        pendingResourceOwner.setActivationCode(UUID.randomUUID().toString());

        BizUser user = resourceOwnerService.createResourceOwner(pendingResourceOwner);
        Assert.assertEquals(create.getEmail(), user.getEmail());
    }

    @Test(expected = BadRequestException.class)
    public void create_ro_which_email_already_exist() {
        BizUser create = getRootResourceOwner();
        create.setPassword(UUID.randomUUID().toString());
        PendingUser pendingResourceOwner = new PendingUser();
        pendingResourceOwner.setEmail(create.getEmail());
        pendingResourceOwner.setPassword(create.getPassword());
        pendingResourceOwner.setActivationCode(UUID.randomUUID().toString());

        Mockito.doReturn(create).when(userRepo).findOneByEmail(any(String.class));

        BizUser user = resourceOwnerService.createResourceOwner(pendingResourceOwner);
    }

    private BizUser getRootResourceOwner() {
        BizUser resourceOwner = new BizUser();
        try {
            Field id = BizUser.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(resourceOwner, new Random().nextLong());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        resourceOwner.setEmail(UUID.randomUUID().toString() + "@gmail.com");
        GrantedAuthorityImpl authority = getAuthority(BizUserAuthorityEnum.ROLE_ROOT);
        resourceOwner.setGrantedAuthorities(Collections.singletonList(authority));
        return resourceOwner;
    }

    private BizUser getNonRootResourceOwner() {
        BizUser resourceOwner = new BizUser();
        try {
            Field id = BizUser.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(resourceOwner, new Random().nextLong());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        resourceOwner.setEmail(UUID.randomUUID().toString() + "@gmail.com");
        GrantedAuthorityImpl authority = getAuthority(BizUserAuthorityEnum.ROLE_USER);
        resourceOwner.setGrantedAuthorities(Collections.singletonList(authority));
        return resourceOwner;
    }

    private GrantedAuthorityImpl getAuthority(BizUserAuthorityEnum resourceOwnerAuthorityEnum) {
        return GrantedAuthorityImpl.getGrantedAuthority(BizUserAuthorityEnum.class, resourceOwnerAuthorityEnum.toString());
    }
}
