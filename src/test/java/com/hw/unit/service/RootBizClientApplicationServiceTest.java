package com.hw.unit.service;

import com.hw.aggregate.client.BizClientRepo;
import com.hw.aggregate.client.RevokeBizClientTokenService;
import com.hw.aggregate.client.RootBIzClientApplicationService;
import com.hw.aggregate.client.model.*;
import com.hw.shared.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.HashSet;

@RunWith(MockitoJUnitRunner.class)
public class RootBizClientApplicationServiceTest {
    @InjectMocks
    RootBIzClientApplicationService clientService = new RootBIzClientApplicationService();
    @Mock
    BizClientRepo clientRepo;
    @Mock
    IdGenerator idGenerator;
    @Mock
    RevokeBizClientTokenService tokenRevocationService;
    @Mock
    BCryptPasswordEncoder encoder;
    private String STUB_RESOURCE_ID = "dummyResourceId";

//    @Test
//    public void get_all_clients() {
//        ArrayList<BizClient> clients = new ArrayList<>();
//        Mockito.doReturn(clients).when(clientRepo).findAll();
//        List<BizClient> clients1 = clientService.readClients();
//        Assert.assertEquals(0, clients1.size());
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void delete_non_exist_client() {
//        Mockito.doReturn(Optional.empty()).when(clientRepo).findById(any(Long.class));
//        clientService.deleteClient(new Random().nextLong());
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void root_client_cannot_be_deleted() {
//        BizClient client = getClient(UUID.randomUUID().toString());
//        GrantedAuthorityImpl authority = getAuthority(BizClientAuthorityEnum.ROLE_ROOT);
//        client.setGrantedAuthorities(Collections.singletonList(authority));
//        Mockito.doReturn(Optional.of(client)).when(clientRepo).findById(any(Long.class));
//        clientService.deleteClient(new Random().nextLong());
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void replace_client_with_invalid_client_for_empty_resourceId() {
//        BizClient client = getClient(UUID.randomUUID().toString());
//        BizClient client2 = getClient(UUID.randomUUID().toString());
//        client.setResourceIds(new HashSet<>());
//        clientService.replaceClient(client, new Random().nextLong());
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void replace_client_with_invalid_client_for_invalid_resourceId() {
//        BizClient newClient = getClient(UUID.randomUUID().toString());
//        BizClient mockClient = getClient(UUID.randomUUID().toString());
//        mockClient.setResourceIndicator(Boolean.FALSE);
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findByClientId(any(String.class));
//        clientService.replaceClient(newClient, new Random().nextLong());
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void replace_client_with_invalid_client_for_not_existing_resourceId() {
//        BizClient newClient = getClient(UUID.randomUUID().toString());
//        BizClient mockClient = getClient(UUID.randomUUID().toString());
//        mockClient.setResourceIndicator(Boolean.FALSE);
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findByClientId(any(String.class));
//        clientService.replaceClient(newClient, new Random().nextLong());
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void replace_client_with_invalid_client_mark_with_resource_indicator_but_has_wrong_authority() {
//        BizClient newClient = getClient(UUID.randomUUID().toString());
//        newClient.setResourceIndicator(Boolean.TRUE);
//        clientService.replaceClient(newClient, new Random().nextLong());
//    }
//
//    @Test
//    public void replace_client_with_valid_client() {
//        BizClient newClient = getClient(UUID.randomUUID().toString());
//        BizClient mockClient = getClient(UUID.randomUUID().toString());
//        mockClient.setResourceIndicator(Boolean.TRUE);
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findByClientId(any(String.class));
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findById(any(Long.class));
//        Mockito.doReturn(false).when(tokenRevocationService).shouldRevoke(any(BizClient.class), any(BizClient.class));
//        Mockito.doNothing().when(tokenRevocationService).blacklist(any(String.class), any(Boolean.class));
//        clientService.replaceClient(newClient, new Random().nextLong());
//        Mockito.verify(encoder, Mockito.times(0)).encode(any(CharSequence.class));
//    }
//
//    @Test
//    public void replace_client_with_valid_client_update_pwd() {
//        BizClient newClient = getClient(UUID.randomUUID().toString());
//        newClient.setClientSecret(UUID.randomUUID().toString());
//        BizClient mockClient = getClient(UUID.randomUUID().toString());
//        mockClient.setResourceIndicator(Boolean.TRUE);
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findByClientId(any(String.class));
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findById(any(Long.class));
//        Mockito.doReturn(false).when(tokenRevocationService).shouldRevoke(any(BizClient.class), any(BizClient.class));
//        Mockito.doNothing().when(tokenRevocationService).blacklist(any(String.class), any(Boolean.class));
//        clientService.replaceClient(newClient, new Random().nextLong());
//        Mockito.verify(encoder, Mockito.times(1)).encode(any(CharSequence.class));
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void read_partial_client_by_id_with_null_field() {
//        clientService.readPartialClientById(new Random().nextLong(), null);
//    }
//
//    @Test(expected = BadRequestException.class)
//    public void read_partial_client_by_id_with_not_supported_field() {
//        clientService.readPartialClientById(new Random().nextLong(), UUID.randomUUID().toString());
//    }
//
//    @Test
//    public void read_partial_client_by_id() {
//        BizClient mockClient = getClient(UUID.randomUUID().toString());
//        mockClient.setAutoApprove(Boolean.TRUE);
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findById(any(Long.class));
//        Map<String, String> autoApprove = clientService.readPartialClientById(new Random().nextLong(), "autoApprove");
//        Assert.assertEquals("true", autoApprove.get("autoApprove"));
//    }
//
//    @Test
//    public void read_partial_client_by_id_client_not_support_authorization_code_flow() {
//        BizClient mockClient = getClient(UUID.randomUUID().toString());
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findById(any(Long.class));
//        Map<String, String> autoApprove = clientService.readPartialClientById(new Random().nextLong(), "autoApprove");
//        Assert.assertEquals("false", autoApprove.get("autoApprove"));
//    }
//
//    @Test
//    public void create_client() {
//        BizClient newClient = getClient(UUID.randomUUID().toString());
//        BizClient mockClient = getClient(UUID.randomUUID().toString());
//        mockClient.setResourceIndicator(Boolean.TRUE);
//        Mockito.doReturn(Optional.empty()).when(clientRepo).findByClientId(newClient.getClientId());
//        Mockito.doReturn(Optional.of(mockClient)).when(clientRepo).findByClientId(STUB_RESOURCE_ID);
//        Mockito.doReturn(0L).when(idGenerator).getId();
//        clientService.createClient(newClient);
//        Mockito.verify(encoder, Mockito.times(1)).encode(any(String.class));
//    }

//    private BizClient getClient(String clientId) {
//        BizClient client = new BizClient();
////        client.setClientId(clientId);
//        /** set default to prevent NPE*/
//        client.setScopeEnums(new HashSet<>(Collections.singletonList(ScopeEnum.read)));
//        GrantedAuthorityImpl authority = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
//        client.setResourceIndicator(Boolean.FALSE);
//        client.setGrantedAuthorities(Collections.singletonList(authority));
//        client.setGrantTypeEnums(new HashSet<>(Collections.singletonList(GrantTypeEnum.client_credentials)));
//        client.setAccessTokenValiditySeconds(1000);
//        client.setResourceIds(new HashSet<>(Collections.singletonList(STUB_RESOURCE_ID)));
//        return client;
//    }
//
//    private GrantedAuthorityImpl getAuthority(BizClientAuthorityEnum clientAuthorityEnum) {
//        return GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, clientAuthorityEnum.toString());
//    }
}
