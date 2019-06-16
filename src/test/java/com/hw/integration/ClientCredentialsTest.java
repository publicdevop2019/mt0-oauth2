package com.hw.integration;

import com.hw.OAuth2Service;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAuth2Service.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientCredentialsTest {
    private String password = "password";
    private String client_credentials = "client_credentials";
    private String valid_clientId = "oauth2-id";
    private String valid_clientId_no_secret = "register-id";
    private String valid_clientSecret = "root";
    private String valid_empty_secret = "";
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    int randomServerPort;

    @Test
    public void happy_client_w_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(client_credentials, valid_clientId, valid_clientSecret);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
    }

    @Test
    public void happy_client_w_empty_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(client_credentials, valid_clientId_no_secret, valid_empty_secret);
        Assert.assertNotNull(tokenResponse.getBody().getValue());

    }

    @Test
    public void sad_client_w_wrong_credentials() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(client_credentials, valid_clientId, valid_empty_secret);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void sad_client_w_wrong_grant_type() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, valid_clientId, valid_clientSecret);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void sad_no_exist_client() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(password, UUID.randomUUID().toString(), UUID.randomUUID().toString());
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);

    }

    private ResponseEntity<DefaultOAuth2AccessToken> getTokenResponse(String grantType, String clientId, String clientSecret) {
        String url = "http://localhost:" + randomServerPort + "/" + "oauth/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }
}
