package com.hw;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CORSTests {
    @LocalServerPort
    int randomServerPort;
    @Autowired
    private Environment env;
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    private TestRestTemplate restTemplate;

    @Before
    public void beforeTest() {
        restTemplate = new TestRestTemplate();
    }

    @Test
    public void cors_validHeadrs() throws Exception {
        //NOTE:origin etc restricted headers will not be set by HttpUrlConnection, href:https://stackoverflow.com/questions/41699608/resttemplate-not-passing-origin-header
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String url = "http://localhost:" + randomServerPort + "/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", env.getProperty("config.cors.myprofile"));
        headers.add("Access-Control-Request-Method", "POST");
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<Object> res = restTemplate.exchange(url, HttpMethod.OPTIONS, request, Object.class);
        assertThat(res.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void cors_notAllowedOrigin() throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String url = "http://localhost:" + randomServerPort + "/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:4300");
        headers.add("Access-Control-Request-Method", "POST");
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<?> res = restTemplate.exchange(url, HttpMethod.OPTIONS, request, String.class);
        assertThat(res.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cors_missingRequired_OriginHeader() throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String url = "http://localhost:" + randomServerPort + "/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Request-Method", "POST");
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<?> res = restTemplate.exchange(url, HttpMethod.OPTIONS, request, String.class);
        assertThat(res.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void cors_MissingRequired_RequestMethodHeader() throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String url = "http://localhost:" + randomServerPort + "/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", env.getProperty("config.cors.myprofile"));
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<?> res = restTemplate.exchange(url, HttpMethod.OPTIONS, request, String.class);
        assertThat(res.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void cors_noCORS_Headers() throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String url = "http://localhost:" + randomServerPort + "/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<?> res = restTemplate.exchange(url, HttpMethod.OPTIONS, request, String.class);
        assertThat(res.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
        assertThat(res.getHeaders().getAccessControlAllowOrigin(), is(nullValue()));
    }

    @Test
    public void cors_noCORS_Headers_validClient() throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String url = "http://localhost:" + randomServerPort + "/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        //Build the auth-header
        final String auth = env.getProperty("config.client.myprofile.id") + ":" + env.getProperty("config.client.myprofile.secret");
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        final String authHeader = "Basic " + new String(encodedAuth);
        headers.add("Authorization", authHeader);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<?> res = restTemplate.exchange(url, HttpMethod.OPTIONS, request, String.class);
        assertThat(res.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(res.getHeaders().getAccessControlAllowOrigin(), is(nullValue()));
    }

}
