package com.hw.integration;

import com.hw.Application;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PasswordFlowTest {
    private String password = "password";
    private String client_credentials = "client_credentials";
    private String valid_clientId = "login-id";
    private String valid_clientId_no_refersh = "test-id";
    private String valid_empty_secret = "";
    private String valid_username_root = "haolinwei2015@gmail.com";
    private String valid_username_admin = "haolinwei2017@gmail.com";
    private String valid_username_user = "haolinwei2018@gmail.com";
    private String valid_pwd = "root";
    private String invalid_username = "root2@gmail.com";
    private String invalid_clientId = "root2";
    private TestRestTemplate restTemplate = new TestRestTemplate();

    /**
     * parse jwt token
     */
    @Autowired
    JwtTokenStore jwtTokenStore;

    @LocalServerPort
    int randomServerPort;

    @Test
    public void happy_getAccessToken_n_refreshToken() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNotNull(tokenResponse.getBody().getRefreshToken().getValue());
    }

    @Test
    public void happy_check_root_jwt() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNotNull(tokenResponse.getBody().getRefreshToken().getValue());
        OAuth2Authentication oAuth2Authentication = jwtTokenStore.readAuthentication(tokenResponse.getBody());
        Collection<GrantedAuthority> authorities = oAuth2Authentication.getAuthorities();
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_USER.toString())).count());
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString())).count());
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ROOT.toString())).count());
    }

    @Test
    public void happy_check_admin_jwt() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_admin, valid_pwd, valid_clientId, valid_empty_secret);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNotNull(tokenResponse.getBody().getRefreshToken().getValue());
        OAuth2Authentication oAuth2Authentication = jwtTokenStore.readAuthentication(tokenResponse.getBody());
        Collection<GrantedAuthority> authorities = oAuth2Authentication.getAuthorities();
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_USER.toString())).count());
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString())).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ROOT.toString())).count());
    }

    @Test
    public void happy_check_user_jwt() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_user, valid_pwd, valid_clientId, valid_empty_secret);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNotNull(tokenResponse.getBody().getRefreshToken().getValue());
        OAuth2Authentication oAuth2Authentication = jwtTokenStore.readAuthentication(tokenResponse.getBody());
        Collection<GrantedAuthority> authorities = oAuth2Authentication.getAuthorities();
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_USER.toString())).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString())).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ROOT.toString())).count());
    }

    @Test
    public void happy_getAccessToken_only() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, valid_clientId_no_refersh, valid_empty_secret);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNull(tokenResponse.getBody().getRefreshToken());
    }

    @Test
    public void sad_getAccessToken_w_invalidUserCredentials() {
        ResponseEntity<?> tokenResponse = getTokenResponse(password, invalid_username, valid_pwd, valid_clientId, valid_empty_secret);
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void sad_getAccessToken_w_invalidClientDetails() {
        ResponseEntity<?> tokenResponse = getTokenResponse(password, valid_username_root, valid_pwd, invalid_clientId, valid_empty_secret);
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void sad_getAccessToken_w_invalidGrantType() {
        ResponseEntity<?> tokenResponse = getTokenResponse(client_credentials, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
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

}
