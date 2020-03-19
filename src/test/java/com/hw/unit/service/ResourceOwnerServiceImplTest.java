package com.hw.unit.service;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.PendingResourceOwner;
import com.hw.entity.ResourceOwner;
import com.hw.interfaze.TokenRevocationService;
import com.hw.repo.PendingResourceOwnerRepo;
import com.hw.repo.ResourceOwnerRepo;
import com.hw.service.ResourceOwnerServiceImpl;
import com.hw.shared.BadRequestException;
import org.junit.Assert;
import org.junit.Ignore;
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
public class ResourceOwnerServiceImplTest {
    private String AUTHORIZATION_HEADER_ROOT = "xx.eyJ1aWQiOiIwIiwiYXVkIjpbInByb2R1Y3QiLCJmaWxlLXVwbG9hZCIsImVkZ2UtcHJveHkiLCJ1c2VyLXByb2ZpbGUiLCJvYXV0aDItaWQiXSwidXNlcl9uYW1lIjoiaGFvbGlud2VpMjAxNUBnbWFpbC5jb20iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE1ODIyNTU2NjQsImlhdCI6MTU4MjI1NTU0NCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9ST09UIiwiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiJmOTI1YzdhOC0xMGU2LTQ1OGMtYjg1MS0zMmM3YjllMDQ2OTgiLCJjbGllbnRfaWQiOiJsb2dpbi1pZCJ9.zz";
    private String AUTHORIZATION_HEADER_ADMIN = "xx.eyJ1aWQiOiIwIiwiYXVkIjpbInByb2R1Y3QiLCJmaWxlLXVwbG9hZCIsImVkZ2UtcHJveHkiLCJ1c2VyLXByb2ZpbGUiLCJvYXV0aDItaWQiXSwidXNlcl9uYW1lIjoiaGFvbGlud2VpMjAxNUBnbWFpbC5jb20iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE1ODIyNTU2NjQsImlhdCI6MTU4MjI1NTU0NCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiJmOTI1YzdhOC0xMGU2LTQ1OGMtYjg1MS0zMmM3YjllMDQ2OTgiLCJjbGllbnRfaWQiOiJsb2dpbi1pZCJ9.zz";
    private String AUTHORIZATION_HEADER_USER = "xx.eyJ1aWQiOiIwIiwiYXVkIjpbInByb2R1Y3QiLCJmaWxlLXVwbG9hZCIsImVkZ2UtcHJveHkiLCJ1c2VyLXByb2ZpbGUiLCJvYXV0aDItaWQiXSwidXNlcl9uYW1lIjoiaGFvbGlud2VpMjAxNUBnbWFpbC5jb20iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0ZSJdLCJleHAiOjE1ODIyNTU2NjQsImlhdCI6MTU4MjI1NTU0NCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6ImY5MjVjN2E4LTEwZTYtNDU4Yy1iODUxLTMyYzdiOWUwNDY5OCIsImNsaWVudF9pZCI6ImxvZ2luLWlkIn0=.zz";
    @InjectMocks
    ResourceOwnerServiceImpl resourceOwnerService = new ResourceOwnerServiceImpl();

    @Mock
    ResourceOwnerRepo userRepo;

    @Mock
    PendingResourceOwnerRepo pendingResourceOwnerRepo;

    @Mock
    BCryptPasswordEncoder encoder;

    @Mock
    TokenRevocationService<ResourceOwner> tokenRevocationService;

    @Test
    public void get_all_ro() {
        ArrayList<ResourceOwner> resourceOwners1 = new ArrayList<>();
        Mockito.doReturn(resourceOwners1).when(userRepo).findAll();
        List<ResourceOwner> resourceOwners = resourceOwnerService.readAllResourceOwners();
        Assert.assertEquals(0, resourceOwners.size());
    }

    @Test(expected = BadRequestException.class)
    public void delete_not_exist_ro() {
        Mockito.doReturn(Optional.empty()).when(userRepo).findById(any(Long.class));
        resourceOwnerService.deleteResourceOwner(new Random().nextLong());
    }

    @Test(expected = BadRequestException.class)
    public void delete_root_ro() {
        ResourceOwner resourceOwner = getRootResourceOwner();
        Mockito.doReturn(Optional.of(resourceOwner)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.deleteResourceOwner(new Random().nextLong());
    }

    @Test(expected = BadRequestException.class)
    public void root_ro_should_not_get_update() {
        ResourceOwner resourceOwner = getRootResourceOwner();
        Mockito.doReturn(Optional.of(resourceOwner)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(resourceOwner, new Random().nextLong(), UUID.randomUUID().toString());
    }

    @Test(expected = BadRequestException.class)
    public void no_ro_can_be_update_to_root() {
        ResourceOwner stored = getRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        stored.setGrantedAuthorities(Collections.singletonList(authority));
        ResourceOwner update = getRootResourceOwner();
        GrantedAuthorityImpl authority2 = getAuthority(ResourceOwnerAuthorityEnum.ROLE_ROOT);
        update.setGrantedAuthorities(Collections.singletonList(authority2));
        Mockito.doReturn(Optional.of(stored)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(update, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test(expected = BadRequestException.class)
    public void admin_ro_trying_to_set_ro_to_admin() {
        ResourceOwner resourceOwner = getRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        resourceOwner.setGrantedAuthorities(Collections.singletonList(authority));
        Mockito.doReturn(Optional.of(resourceOwner)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(resourceOwner, new Random().nextLong(), AUTHORIZATION_HEADER_ADMIN);
    }

    @Test(expected = BadRequestException.class)
    public void admin_ro_trying_to_set_ro_subscribe_new_order() {
        ResourceOwner updateRO = getNonRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl authority2 = getAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        updateRO.setGrantedAuthorities(List.of(authority, authority2));
        updateRO.setSubscription(Boolean.TRUE);
        Mockito.doReturn(Optional.of(updateRO)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(updateRO, new Random().nextLong(), AUTHORIZATION_HEADER_ADMIN);
    }

    @Test
    public void root_ro_trying_to_set_ro_subscribe_new_order_for_admin() {
        ResourceOwner updateRO = getNonRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl authority2 = getAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        updateRO.setGrantedAuthorities(List.of(authority, authority2));
        updateRO.setSubscription(Boolean.TRUE);
        Mockito.doReturn(Optional.of(updateRO)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(updateRO, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test(expected = BadRequestException.class)
    public void root_ro_trying_to_set_ro_subscribe_new_order_for_user() {
        ResourceOwner updateRO = getNonRootResourceOwner();
        GrantedAuthorityImpl authority2 = getAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        updateRO.setGrantedAuthorities(List.of(authority2));
        updateRO.setSubscription(Boolean.TRUE);
        Mockito.doReturn(Optional.of(updateRO)).when(userRepo).findById(any(Long.class));
        resourceOwnerService.updateResourceOwner(updateRO, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test
    public void root_ro_trying_to_set_ro_to_admin() {
        ResourceOwner stored = getRootResourceOwner();
        GrantedAuthorityImpl authority = getAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        stored.setGrantedAuthorities(Collections.singletonList(authority));
        ResourceOwner update = getRootResourceOwner();
        GrantedAuthorityImpl authority2 = getAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        update.setGrantedAuthorities(Collections.singletonList(authority2));
        Mockito.doReturn(Optional.of(stored)).when(userRepo).findById(any(Long.class));
        Mockito.doNothing().when(tokenRevocationService).blacklist(any(String.class), any(Boolean.class));
        Mockito.doReturn(false).when(tokenRevocationService).shouldRevoke(any(ResourceOwner.class), any(ResourceOwner.class));
        resourceOwnerService.updateResourceOwner(update, new Random().nextLong(), AUTHORIZATION_HEADER_ROOT);
    }

    @Test
    public void create_ro() {
        ResourceOwner create = getRootResourceOwner();
        create.setPassword(UUID.randomUUID().toString());
        PendingResourceOwner pendingResourceOwner = new PendingResourceOwner();
        pendingResourceOwner.setEmail(create.getEmail());
        pendingResourceOwner.setPassword(create.getPassword());
        pendingResourceOwner.setActivationCode(UUID.randomUUID().toString());

        Mockito.doReturn(null).when(userRepo).findOneByEmail(any(String.class));
        Mockito.doReturn(pendingResourceOwner).when(pendingResourceOwnerRepo).findOneByEmail(any(String.class));
        Mockito.doReturn(create).when(userRepo).save(any(ResourceOwner.class));
        Mockito.doReturn(UUID.randomUUID().toString()).when(encoder).encode(any(String.class));

        ResourceOwner user = resourceOwnerService.createResourceOwner(pendingResourceOwner);
        Assert.assertEquals(create.getEmail(), user.getEmail());
    }

    @Test(expected = BadRequestException.class)
    public void create_ro_with_invalid_payload() {
        ResourceOwner create = getRootResourceOwner();
        create.setPassword(UUID.randomUUID().toString());
        PendingResourceOwner pendingResourceOwner = new PendingResourceOwner();
        pendingResourceOwner.setEmail(create.getEmail());

        pendingResourceOwner.setActivationCode(UUID.randomUUID().toString());

        ResourceOwner user = resourceOwnerService.createResourceOwner(pendingResourceOwner);
        Assert.assertEquals(create.getEmail(), user.getEmail());
    }

    @Test(expected = BadRequestException.class)
    public void create_ro_which_email_already_exist() {
        ResourceOwner create = getRootResourceOwner();
        create.setPassword(UUID.randomUUID().toString());
        PendingResourceOwner pendingResourceOwner = new PendingResourceOwner();
        pendingResourceOwner.setEmail(create.getEmail());
        pendingResourceOwner.setPassword(create.getPassword());
        pendingResourceOwner.setActivationCode(UUID.randomUUID().toString());

        Mockito.doReturn(create).when(userRepo).findOneByEmail(any(String.class));

        ResourceOwner user = resourceOwnerService.createResourceOwner(pendingResourceOwner);
    }

    private ResourceOwner getRootResourceOwner() {
        ResourceOwner resourceOwner = new ResourceOwner();
        try {
            Field id = ResourceOwner.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(resourceOwner, new Random().nextLong());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        resourceOwner.setEmail(UUID.randomUUID().toString() + "@gmail.com");
        GrantedAuthorityImpl authority = getAuthority(ResourceOwnerAuthorityEnum.ROLE_ROOT);
        resourceOwner.setGrantedAuthorities(Collections.singletonList(authority));
        return resourceOwner;
    }

    private ResourceOwner getNonRootResourceOwner() {
        ResourceOwner resourceOwner = new ResourceOwner();
        try {
            Field id = ResourceOwner.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(resourceOwner, new Random().nextLong());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        resourceOwner.setEmail(UUID.randomUUID().toString() + "@gmail.com");
        GrantedAuthorityImpl authority = getAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        resourceOwner.setGrantedAuthorities(Collections.singletonList(authority));
        return resourceOwner;
    }

    private GrantedAuthorityImpl getAuthority(ResourceOwnerAuthorityEnum resourceOwnerAuthorityEnum) {
        return GrantedAuthorityImpl.getGrantedAuthority(ResourceOwnerAuthorityEnum.class, resourceOwnerAuthorityEnum.toString());
    }
}
