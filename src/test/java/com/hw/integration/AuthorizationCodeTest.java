package com.hw.integration;

import com.hw.OAuth2Service;
import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.jayway.jsonpath.JsonPath;
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
import java.util.Objects;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAuth2Service.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class AuthorizationCodeTest {

    private String password = "password";
    private String authorization_code = "authorization_code";
    private String client_credentials = "client_credentials";
    private String valid_clientId = "login-id";
    private String valid_third_party = "mgfb-id";
    private String valid_clientId_no_refersh = "test-id";
    private String valid_empty_secret = "";
    private String valid_username_root = "root@gmail.com";
    private String valid_username_admin = "admin@gmail.com";
    private String valid_username_user = "user@gmail.com";
    private String valid_pwd = "root";
    private String invalid_username = "root2@gmail.com";
    private String invalid_clientId = "root2";
    private String valid_redirect_uri = "http://localhost:4200";
    private String state = "login";
    private String response_type = "code";
    private String invalid_accessToken = UUID.randomUUID().toString();

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    int randomServerPort;
    /**
     * parse jwt token
     */
    @Autowired
    JwtTokenStore jwtTokenStore;

    @Test
    public void happy_getAuthorizationCode_root() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> code = getCodeResp(valid_third_party, accessToken);
        String body = code.getBody();
        String read = JsonPath.read(body, "$.authorize_code");
        Assert.assertNotNull(read);

    }

    @Test
    public void happy_getAuthorizationCode_admin() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_admin, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = getCodeResp(valid_third_party, accessToken);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");

        Assert.assertNotNull(code);

        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(authorization_code, code, valid_redirect_uri, valid_third_party);

        Assert.assertEquals(HttpStatus.OK, authorizationToken.getStatusCode());
        Assert.assertNotNull(authorizationToken.getBody());

        OAuth2Authentication oAuth2Authentication = jwtTokenStore.readAuthentication(authorizationToken.getBody());
        String name = oAuth2Authentication.getUserAuthentication().getName();
        Collection<GrantedAuthority> authorities = oAuth2Authentication.getAuthorities();


        Assert.assertEquals(false, oAuth2Authentication.isClientOnly());
        Assert.assertEquals(valid_username_admin, name);
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_USER.toString())).count());
        Assert.assertEquals(1, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ADMIN.toString())).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.getAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ROOT.toString())).count());
        Assert.assertEquals(0, authorities.stream().map(e -> {
                    try {
                        return ClientAuthorityEnum.valueOf(e.getAuthority());
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                }


        ).filter(Objects::nonNull).count());

    }

    @Test
    public void happy_getAuthorizationCode_user() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_user, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = getCodeResp(valid_third_party, accessToken);
        String read = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        Assert.assertNotNull(read);

    }

    @Test
    public void sad_invalid_bearerToken() {
        ResponseEntity<String> code = getCodeResp(valid_third_party, invalid_accessToken);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, code.getStatusCode());

    }

    @Test
    public void sad_invalid_code() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> code = getCodeResp(valid_third_party, accessToken);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(authorization_code, UUID.randomUUID().toString(), valid_redirect_uri, valid_third_party);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void sad_invalid_redirectUri() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = getCodeResp(valid_third_party, accessToken);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(authorization_code, code, UUID.randomUUID().toString(), valid_third_party);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void sad_invalid_grantType() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = getCodeResp(valid_third_party, accessToken);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(password, code, valid_redirect_uri, valid_third_party);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void sad_invalid_clientId_after_code_gen() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = getCodeResp(valid_third_party, accessToken);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(authorization_code, code, valid_redirect_uri, valid_clientId);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void sad_invalid_client_credential() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = pwdFlowLogin(password, valid_username_root, valid_pwd, valid_clientId, valid_empty_secret);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = getCodeResp(valid_third_party, accessToken);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationTokenSecret(authorization_code, code, valid_redirect_uri, valid_clientId, UUID.randomUUID().toString());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }


    private ResponseEntity<String> getCodeResp(String clientId, String bearerToken) {
        String url = "http://localhost:" + randomServerPort + "/" + "v1/api/" + "authorize";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("response_type", response_type);
        params.add("client_id", clientId);
        params.add("state", state);
        params.add("redirect_uri", valid_redirect_uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    private ResponseEntity<DefaultOAuth2AccessToken> pwdFlowLogin(String grantType, String username, String userPwd, String clientId, String clientSecret) {
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

    private ResponseEntity<DefaultOAuth2AccessToken> getAuthorizationToken(String grantType, String code, String redirect_uri, String clientId) {
        return getAuthorizationTokenSecret(grantType, code, redirect_uri, clientId, valid_empty_secret);
    }

    private ResponseEntity<DefaultOAuth2AccessToken> getAuthorizationTokenSecret(String grantType, String code, String redirect_uri, String clientId, String clientSecret) {
        String url = "http://localhost:" + randomServerPort + "/" + "oauth/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("code", code);
        params.add("redirect_uri", redirect_uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }
}
