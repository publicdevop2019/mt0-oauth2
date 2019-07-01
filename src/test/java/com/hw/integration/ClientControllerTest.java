package com.hw.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.OAuth2Service;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.clazz.eenum.GrantTypeEnum;
import com.hw.clazz.eenum.ScopeEnum;
import com.hw.entity.Client;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAuth2Service.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerTest {

    private String password = "password";
    private String valid_clientId = "login-id";
    private String valid_resourceId = "resource-id";
    private String invalid_resourceId = "test-id";
    private String invalid_resourceId_not_found = "test-idasdf";
    private String valid_empty_secret = "";
    private String valid_username_root = "root@gmail.com";
    private String valid_username_admin = "admin@gmail.com";
    private String valid_pwd = "root";
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    int randomServerPort;

    @Test
    public void sad_createClient_no_resourceId() throws JsonProcessingException {
        Client client = getClientAsNonResource();
        ResponseEntity<String> exchange = createClient(client);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void sad_createInvalidClient_w_resourceId_as_resource() throws JsonProcessingException {
        Client client = getInvalidClientAsResource();
        ResponseEntity<String> exchange = createClient(client);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void happy_createClient_w_resourceId_none_resource() throws JsonProcessingException {
        Client client = getClientAsNonResource(valid_resourceId);
        ResponseEntity<String> exchange = createClient(client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = getTokenResponse(password, valid_username_root, valid_pwd, client.getClientId(), client.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void happy_createClient_w_resourceId_as_resource() throws JsonProcessingException {
        Client client = getClientAsResource(valid_resourceId);
        ResponseEntity<String> exchange = createClient(client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = getTokenResponse(password, valid_username_root, valid_pwd, client.getClientId(), client.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void sad_createClient_w_resourceId_as_resource() throws JsonProcessingException {
        Client client = getClientAsResource(invalid_resourceId);
        ResponseEntity<String> exchange = createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void sad_createClient_w_invalid_resourceId() throws JsonProcessingException {
        Client client = getClientAsNonResource(invalid_resourceId_not_found);
        ResponseEntity<String> exchange = createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void sad_createClient_w_admin_account() throws JsonProcessingException {
        Client client = getClientAsNonResource(valid_resourceId);
        String url = "http://localhost:" + randomServerPort + "/api/v1" + "/client";
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_admin, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s = mapper.writeValueAsString(client);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
    }

    @Test
    public void happy_readClients() {
        String url = "http://localhost:" + randomServerPort + "/api/v1" + "/clients";
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ParameterizedTypeReference<List<Client>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<Client>> exchange = restTemplate.exchange(url, HttpMethod.GET, request, responseType);
        Assert.assertNotSame(0, exchange.getBody().size());
    }

    @Test
    public void happy_replaceClient_noUpdateSecret() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = getClientAsNonResource(valid_resourceId);
        ResponseEntity<String> client1 = createClient(oldClient);
        String url = "http://localhost:" + randomServerPort + "/api/v1" + "/client/" + client1.getHeaders().getLocation().toString();
        Client newClient = getClientAsNonResource(valid_resourceId);
        newClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s1 = mapper.writeValueAsString(newClient);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = getTokenResponse(password, valid_username_root, valid_pwd, newClient.getClientId(), oldClient.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void sad_replaceClient_noUpdateSecret_w_invalid_as_resource() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = getClientAsNonResource(valid_resourceId);
        ResponseEntity<String> client1 = createClient(oldClient);
        String url = "http://localhost:" + randomServerPort + "/api/v1" + "/client/" + client1.getHeaders().getLocation().toString();
        Client newClient = getInvalidClientAsResource(valid_resourceId);
        newClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s1 = mapper.writeValueAsString(newClient);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

    }

    @Test
    public void happy_replaceClient_updateSecret() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = getClientAsNonResource(valid_resourceId);
        ResponseEntity<String> client1 = createClient(oldClient);
        String url = "http://localhost:" + randomServerPort + "/api/v1" + "/client/" + client1.getHeaders().getLocation().toString();
        Client newClient = getClientAsNonResource(valid_resourceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s1 = mapper.writeValueAsString(newClient);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = getTokenResponse(password, valid_username_root, valid_pwd, newClient.getClientId(), newClient.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void happy_deleteClient() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = getClientAsNonResource(valid_resourceId);
        ResponseEntity<String> client1 = createClient(oldClient);
        String url = "http://localhost:" + randomServerPort + "/api/v1" + "/client/" + client1.getHeaders().getLocation().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = getTokenResponse(password, valid_username_root, valid_pwd, oldClient.getClientId(), oldClient.getClientSecret());

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse1.getStatusCode());
    }


    private ResponseEntity<DefaultOAuth2AccessToken> getTokenResponse(String grantType, String username, String userPwd, String clientId, String clientSecret) {
        String url = "http://localhost:" + randomServerPort + "/" + "oauth/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", userPwd);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }

    /**
     * @return different password client obj
     */
    private Client getClientAsNonResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority.setGrantedAuthority(ClientAuthorityEnum.ROLE_BACKEND);
        client.setGrantedAuthorities(Arrays.asList(clientAuthorityEnumGrantedAuthority));
        client.setResourceIndicator(false);
        return client;
    }

    private Client getClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority.setGrantedAuthority(ClientAuthorityEnum.ROLE_FIRST_PARTY);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority2.setGrantedAuthority(ClientAuthorityEnum.ROLE_BACKEND);
        client.setGrantedAuthorities(Arrays.asList(clientAuthorityEnumGrantedAuthority, clientAuthorityEnumGrantedAuthority2));
        client.setResourceIndicator(true);
        return client;
    }

    private Client getInvalidClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority.setGrantedAuthority(ClientAuthorityEnum.ROLE_THIRD_PARTY);
        GrantedAuthorityImpl<ClientAuthorityEnum> clientAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        clientAuthorityEnumGrantedAuthority2.setGrantedAuthority(ClientAuthorityEnum.ROLE_BACKEND);
        client.setGrantedAuthorities(Arrays.asList(clientAuthorityEnumGrantedAuthority));
        client.setResourceIndicator(true);
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

    public ResponseEntity<String> createClient(Client client) throws JsonProcessingException {
        String url = "http://localhost:" + randomServerPort + "/api/v1" + "/client";
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s = mapper.writeValueAsString(client);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }
}