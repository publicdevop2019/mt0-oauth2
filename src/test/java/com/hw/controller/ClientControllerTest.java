package com.hw.controller;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.clazz.eenum.GrantTypeEnum;
import com.hw.clazz.eenum.ScopeEnum;
import com.hw.entity.Client;
import com.hw.repo.OAuthClientRepo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ClientControllerTest {

    @InjectMocks
    ClientController clientController = new ClientController();

    @Mock
    OAuthClientRepo oAuthClientRepo;

    @Mock
    BCryptPasswordEncoder encoder;

    @Test
    public void happy_validateResourceId_hasResourceId() {
        Client validateClient = getClientAsValidResource();
        Client createClient = getClientAsValidResource(validateClient.getClientId());
        Mockito.when(oAuthClientRepo.findByClientId(createClient.getClientId())).thenReturn(null);
        Mockito.when(oAuthClientRepo.findByClientId(validateClient.getClientId())).thenReturn(validateClient);
        Mockito.when(oAuthClientRepo.save(any(Client.class))).thenReturn(createClient);
        Mockito.when(encoder.encode(anyString())).thenReturn(UUID.randomUUID().toString());
        ResponseEntity<?> client1 = clientController.createClient(createClient);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sad_validateResourceId_no_resourceId() {
        Client createClient = getClientAsValidResource();
        clientController.createClient(createClient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sad_validateResourceId_not_found_resourceId() {
        Client createClient = getClientAsValidResource(UUID.randomUUID().toString());
        Mockito.when(oAuthClientRepo.findByClientId(anyString())).thenReturn(null);
        clientController.createClient(createClient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sad_validateResourceId_wrong_resourceId() {
        Client validateClient = getNoneResourceClient();
        Client createClient = getClientAsValidResource(validateClient.getClientId());
        Mockito.when(oAuthClientRepo.findByClientId(validateClient.getClientId())).thenReturn(validateClient);
        clientController.createClient(createClient);
    }


    @Test
    public void happy_validateAuthority_client_as_resource() {
        Client validateClient = getClientAsValidResource();
        Client createClient = getClientAsValidResource(validateClient.getClientId());
        Mockito.when(oAuthClientRepo.findByClientId(createClient.getClientId())).thenReturn(null);
        Mockito.when(oAuthClientRepo.findByClientId(validateClient .getClientId())).thenReturn(validateClient );
        Mockito.when(oAuthClientRepo.save(any(Client.class))).thenReturn(createClient);
        Mockito.when(encoder.encode(anyString())).thenReturn(UUID.randomUUID().toString());
        ResponseEntity<?> client1 = clientController.createClient(createClient);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());

    }

    @Test
    public void happy_client_not_resource() {
        Client validateClient = getClientAsValidResource();
        Client createClient = getNoneResourceClient(validateClient.getClientId());
        Mockito.when(oAuthClientRepo.findByClientId(createClient.getClientId())).thenReturn(null);
        Mockito.when(oAuthClientRepo.findByClientId(validateClient.getClientId())).thenReturn(validateClient);
        Mockito.when(oAuthClientRepo.save(any(Client.class))).thenReturn(createClient);
        Mockito.when(encoder.encode(anyString())).thenReturn(UUID.randomUUID().toString());
        ResponseEntity<?> client1 = clientController.createClient(createClient);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());

    }

    @Test(expected = IllegalArgumentException.class)
    public void sad_invalidateAuthority_client_as_resource() {
        Client createClient = getClientAsInvalidResource();
        clientController.createClient(createClient);

    }

    private Client getClientAsValidResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority.setGrantedAuthority(ClientAuthorityEnum.ROLE_BACKEND);
        clientAuthorityEnumGrantedAuthority2.setGrantedAuthority(ClientAuthorityEnum.ROLE_FIRST_PARTY);
        client.setGrantedAuthority(Arrays.asList(clientAuthorityEnumGrantedAuthority, clientAuthorityEnumGrantedAuthority2));
        client.setResourceIndicator(true);
        return client;
    }

    private Client getClientAsInvalidResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority2.setGrantedAuthority(ClientAuthorityEnum.ROLE_FIRST_PARTY);
        client.setGrantedAuthority(Arrays.asList(clientAuthorityEnumGrantedAuthority2));
        client.setResourceIndicator(true);
        return client;
    }

    private Client getNoneResourceClient(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority2.setGrantedAuthority(ClientAuthorityEnum.ROLE_FIRST_PARTY);
        client.setGrantedAuthority(Arrays.asList(clientAuthorityEnumGrantedAuthority2));
        client.setResourceIndicator(false);
        return client;
    }

    private Client getClientRaw(String... resourceIds) {
        Client client = new Client();
        client.setClientId(UUID.randomUUID().toString().replace("-", ""));
        client.setClientSecret(UUID.randomUUID().toString().replace("-", ""));
        client.setGrantTypeEnums(new HashSet<>(Arrays.asList(GrantTypeEnum.password)));
        client.setScopeEnums(new HashSet<>(Arrays.asList(ScopeEnum.read)));
        client.setAccessTokenValiditySeconds(1800);
        client.setRefreshTokenValiditySeconds(null);
        client.setHasSecret(true);
        client.setResourceIds(new HashSet<>(Arrays.asList(resourceIds)));
        return client;
    }
}