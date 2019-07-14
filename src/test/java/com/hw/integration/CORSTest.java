package com.hw.integration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * this test suits requires cors profile to be added
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev,h2,cors")
public class CORSTest {

    @LocalServerPort
    private int randomServerPort;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private String thirdPartyOrigin = "https://editor.swagger.io";

    private String apiVersion = "/api/v1/";

    private String[] corsUris = {"oauth/token", "oauth/token_key", "client", "client/0", "clients",
            "authorize", "resourceOwner", "resourceOwner/0", "resourceOwner/pwd", "resourceOwners"};

    @Test
    public void happy_oauthToken() {
        String url = "http://localhost:" + randomServerPort + "/" + corsUris[0];
        ResponseEntity<?> res = sendValidCorsForTokenUri(url);
        corsAssertToken(res);
    }

    @Test
    public void happy_oauthTokenKey() {
        String url = "http://localhost:" + randomServerPort + "/" + corsUris[1];
        ResponseEntity<?> res = sendValidCorsForTokenUri(url);
        corsAssertToken(res);

    }

    @Test
    public void happy_client() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[2];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.POST);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_client_w_id_put() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[3];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.PUT);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_client_w_id_delete() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[3];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.DELETE);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_clients() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[4];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.GET);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_authorize() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[5];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.POST);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_resourceOwner() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[6];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.POST);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_resourceOwner_id_put() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[7];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.PUT);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_resourceOwner_id_delete() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[7];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.DELETE);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_resourceOwner_id_pwd() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[8];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.PATCH);
        corsAssertNonToken(res);

    }

    @Test
    public void happy_resourceOwners() {
        String url = "http://localhost:" + randomServerPort + apiVersion + corsUris[9];
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.GET);
        corsAssertNonToken(res);

    }

    private ResponseEntity<?> sendValidCorsForTokenUri(String uri) {
        /**
         * origin etc restricted headers will not be set by HttpUrlConnection,
         * ref:https://stackoverflow.com/questions/41699608/resttemplate-not-passing-origin-header
         */
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", thirdPartyOrigin);
        headers.add("Access-Control-Request-Method", "POST");
        headers.add("Access-Control-Request-Headers", "authorization,x-requested-with");
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.OPTIONS, request, String.class);

    }

    private void corsAssertToken(ResponseEntity res) {
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assert.assertEquals("*", res.getHeaders().getAccessControlAllowOrigin());
        Assert.assertEquals("[POST, PATCH, GET, DELETE, PUT, OPTIONS]", res.getHeaders().getAccessControlAllowMethods().toString());
        Assert.assertEquals("[authorization, x-requested-with]", res.getHeaders().getAccessControlAllowHeaders().toString());
        Assert.assertEquals(3600, res.getHeaders().getAccessControlMaxAge());
    }

    private ResponseEntity<?> sendValidCorsForNonTokenUri(String uri, HttpMethod method) {
        /**
         * origin etc restricted headers will not be set by HttpUrlConnection,
         * ref:https://stackoverflow.com/questions/41699608/resttemplate-not-passing-origin-header
         */
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", thirdPartyOrigin);
        headers.add("Access-Control-Request-Method", method.toString());
        headers.add("Access-Control-Request-Headers", "authorization");
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.OPTIONS, request, String.class);

    }

    private void corsAssertNonToken(ResponseEntity res) {
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assert.assertEquals("*", res.getHeaders().getAccessControlAllowOrigin());
        Assert.assertEquals("[POST, PATCH, GET, DELETE, PUT, OPTIONS]", res.getHeaders().getAccessControlAllowMethods().toString());
        Assert.assertEquals("[authorization]", res.getHeaders().getAccessControlAllowHeaders().toString());
        Assert.assertEquals(3600, res.getHeaders().getAccessControlMaxAge());
    }

}
