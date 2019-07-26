package com.hw.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.OAuth2Service;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.service.ClientTokenRevocationService;
import com.hw.service.ResourceOwnerTokenRevocationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAuth2Service.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResourceOwnerControllerTest {
    private String password = "password";
    private String client_credentials = "client_credentials";
    private String valid_clientId = "login-id";
    private String invalid_clientId = "rightRoleNotSufficientResourceId";
    private String valid_register_clientId = "register-id";
    private String valid_empty_secret = "";
    private String valid_username_root = "root@gmail.com";
    private String valid_username_admin = "admin@gmail.com";
    private String valid_username_user = "user@gmail.com";
    private String valid_pwd = "root";
    private Long root_index = 0L;
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * parse jwt token
     */
    @Autowired
    JwtTokenStore jwtTokenStore;

    @SpyBean
    OAuth2RestTemplate oAuth2RestTemplate;

    @SpyBean
    ResourceOwnerTokenRevocationService resourceOwnerTokenRevocationService;


    private TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    int randomServerPort;

    @Value("${feature.token.revocation}")
    private Boolean enabled;

    @Test
    public void happy_createUser() throws JsonProcessingException {
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = createUser(user);

        Assert.assertEquals(HttpStatus.OK, user1.getStatusCode());

        Assert.assertNotNull(user1.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = getTokenResponse(password, user.getEmail(), user.getPassword(), valid_clientId, valid_empty_secret);

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());

        OAuth2Authentication oAuth2Authentication = jwtTokenStore.readAuthentication(tokenResponse12.getBody());
        Collection<GrantedAuthority> authorities = oAuth2Authentication.getAuthorities();

        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_USER.toString())).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString())).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ROOT.toString())).count());

    }

    @Test
    public void sad_createUser_invalid_username() throws JsonProcessingException {
        ResourceOwner user = getUser_wrong_username();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = createUser(user);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, user1.getStatusCode());

    }

    @Test
    public void createUser_w_client_missing_right_role_direct_call() throws JsonProcessingException {
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = createUser(user, invalid_clientId);

        Assert.assertEquals(HttpStatus.OK, user1.getStatusCode());
    }

    @Test
    public void happy_updateUserPwd() throws JsonProcessingException {
        ResponseEntity<String> ok = ResponseEntity.ok("");
        Mockito.doReturn(ok).when(oAuth2RestTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = createUser(user);
        /** Location is not used in this case, root/admin/user can only update their password */
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/pwd";
        String newPassword = UUID.randomUUID().toString().replace("-", "");
        /** Login */
        String oldPassword = user.getPassword();
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(this.password, user.getEmail(), user.getPassword(), valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setPassword(newPassword);
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<Object> exchange = restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenRespons33e = getTokenResponse(this.password, user.getEmail(), oldPassword, valid_clientId, valid_empty_secret);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, tokenRespons33e.getStatusCode());

        Mockito.verify(resourceOwnerTokenRevocationService, Mockito.times(1)).blacklist(anyString(),eq(true));
    }

    @Test
    public void happy_readUsers() {
        ParameterizedTypeReference<List<ResourceOwner>> responseType = new ParameterizedTypeReference<>() {
        };
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwners";
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<ResourceOwner>> exchange = restTemplate.exchange(url, HttpMethod.GET, request, responseType);

        Assert.assertNotSame(0, exchange.getBody().size());
    }


    @Test
    public void happy_updateUser_authority() throws JsonProcessingException {
        ResponseEntity<String> ok = ResponseEntity.ok("");
        Mockito.doReturn(ok).when(oAuth2RestTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = createUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority2.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        user.setGrantedAuthorities(List.of(resourceOwnerAuthorityEnumGrantedAuthority, resourceOwnerAuthorityEnumGrantedAuthority2));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        /**
         * login to verify grantedAuthorities has been changed
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = getTokenResponse(password, user.getEmail(), user.getPassword(), valid_clientId, valid_empty_secret);
        OAuth2Authentication oAuth2Authentication = jwtTokenStore.readAuthentication(tokenResponse1.getBody());
        Collection<GrantedAuthority> authorities = oAuth2Authentication.getAuthorities();
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_USER.toString())).count());
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString())).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ROOT.toString())).count());
        if (enabled)
            Mockito.verify(resourceOwnerTokenRevocationService, Mockito.times(1)).blacklist(anyString(),eq(true));
    }

    @Test
    public void sad_update_root_user_authority() throws JsonProcessingException {
        ResourceOwner user = getUser();
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/" + root_index;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_admin, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);

        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority2.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        user.setGrantedAuthorities(List.of(resourceOwnerAuthorityEnumGrantedAuthority, resourceOwnerAuthorityEnumGrantedAuthority2));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void sad_updateUser_authority_include_root_role() throws JsonProcessingException {
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = createUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_ROOT);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority2.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        user.setGrantedAuthorities(List.of(resourceOwnerAuthorityEnumGrantedAuthority, resourceOwnerAuthorityEnumGrantedAuthority2));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void sad_updateUser_authority_w_admin() throws JsonProcessingException {
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = createUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_admin, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_ADMIN);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority2.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        user.setGrantedAuthorities(List.of(resourceOwnerAuthorityEnumGrantedAuthority, resourceOwnerAuthorityEnumGrantedAuthority2));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void happy_updateUser_lock() throws JsonProcessingException {
        ResponseEntity<String> ok = ResponseEntity.ok("");
        Mockito.doReturn(ok).when(oAuth2RestTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = createUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        GrantedAuthorityImpl<ResourceOwnerAuthorityEnum> resourceOwnerAuthorityEnumGrantedAuthority2 = new GrantedAuthorityImpl<>();
        resourceOwnerAuthorityEnumGrantedAuthority2.setGrantedAuthority(ResourceOwnerAuthorityEnum.ROLE_USER);
        user.setGrantedAuthorities(List.of(resourceOwnerAuthorityEnumGrantedAuthority2));
        user.setLocked(true);
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        /**
         * login to verify account has been locked
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = getTokenResponse(password, user.getEmail(), user.getPassword(), valid_clientId, valid_empty_secret);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tokenResponse1.getStatusCode());

        user.setLocked(false);
        String s3 = mapper.writeValueAsString(user);
        HttpEntity<String> request22 = new HttpEntity<>(s3, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange22 = restTemplate.exchange(url, HttpMethod.PUT, request22, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange22.getStatusCode());

        /**
         * login to verify account has been unlocked
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = getTokenResponse(password, user.getEmail(), user.getPassword(), valid_clientId, valid_empty_secret);
        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());
    }

    @Test
    public void happy_deleteUser() throws JsonProcessingException {
        ResponseEntity<String> ok = ResponseEntity.ok("");
        Mockito.doReturn(ok).when(oAuth2RestTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
        ResourceOwner user = getUser();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = createUser(user);

        String s = user1.getHeaders().getLocation().toString();
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = getTokenResponse(password, user.getEmail(), user.getPassword(), valid_clientId, valid_empty_secret);

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        ResponseEntity<Object> exchange = restTemplate.exchange(url, HttpMethod.DELETE, request, Object.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse123 = getTokenResponse(password, user.getEmail(), user.getPassword(), valid_clientId, valid_empty_secret);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse123.getStatusCode());

        Mockito.verify(resourceOwnerTokenRevocationService, Mockito.times(1)).blacklist(anyString(),eq(true));
    }

    @Test
    public void sad_delete_rootUser() {

        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner/" + root_index;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());

        /**
         * try w root
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        ResponseEntity<Object> exchange = restTemplate.exchange(url, HttpMethod.DELETE, request, Object.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
        /**
         * try w admin, admin can not delete user
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse2 = getTokenResponse(password, valid_username_admin, valid_pwd, valid_clientId, valid_empty_secret);
        HttpHeaders headers2 = new HttpHeaders();
        headers.setBearerAuth(tokenResponse2.getBody().getValue());
        HttpEntity<Object> request2 = new HttpEntity<>(null, headers2);
        ResponseEntity<Object> exchange2 = restTemplate.exchange(url, HttpMethod.DELETE, request2, Object.class);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());

    }

    private ResourceOwner getUser() {
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setPassword(UUID.randomUUID().toString().replace("-", ""));
        resourceOwner.setEmail(UUID.randomUUID().toString().replace("-", "") + "@gmail.com");
        return resourceOwner;
    }

    private ResourceOwner getUser_wrong_username() {
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setPassword(UUID.randomUUID().toString().replace("-", ""));
        resourceOwner.setEmail(UUID.randomUUID().toString());
        return resourceOwner;
    }

    private ResponseEntity<DefaultOAuth2AccessToken> getRegisterTokenResponse(String grantType, String clientId, String clientSecret) {
        String url = "http://localhost:" + randomServerPort + "/" + "oauth/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
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

    private ResponseEntity<DefaultOAuth2AccessToken> createUser(ResourceOwner user) throws JsonProcessingException {
        return createUser(user, valid_register_clientId);
    }

    private ResponseEntity<DefaultOAuth2AccessToken> createUser(ResourceOwner user, String clientId) throws JsonProcessingException {
        String url = "http://localhost:" + randomServerPort + "/v1/api" + "/resourceOwner";
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = getRegisterTokenResponse(client_credentials, clientId, valid_empty_secret);
        String value = registerTokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(value);
        String s = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }
}