package com.hw;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResourceOwnerMinRegLoginTests {
    private static final String host = "http://localhost:";
    @LocalServerPort
    int randomServerPort;
    @Autowired
    private Environment env;
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    //    private String defaultUsername=env.getProperty("config.user.embedded.username");
    private String defaultUsername;
    private String otherUsername = "haolinwei2014@gmail.com";
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        this.restTemplate = new TestRestTemplate();
        defaultUsername = env.getProperty("config.user.embedded.username");
        this.registerAnUser(defaultUsername);
        this.registerAnUser(otherUsername);
    }

    @Test
    public void getAccessToken_validRegisterClient() {
        assertThat(this.getRegToken(), not(nullValue()));
    }

    @Test
    public void registerUser_wrongClientToken() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.getLoginToken());
        String jsonBody = "{\n" +
                "\t\"email\":\"haolinei2017@gmail.com\",\n" +
                "\t\"password\":\"Password1!\"\n" +
                "}";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/api/users", HttpMethod.POST, request, String.class);
        assertThat(resp.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void registerUser_token() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.getRegToken());
        String jsonBody = "{\n" +
                "\t\"email\":\"haolinwei2017@gmail.com\",\n" +
                "\t\"password\":\"Password1!\"\n" +
                "}";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/api/users", HttpMethod.POST, request, String.class);
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void registerAlreadyExistUser_token() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.getRegToken());
        String jsonBody = "{\n" +
                "\t\"email\":\"" + defaultUsername + "\",\n" +
                "\t\"password\":\"Password1!\"\n" +
                "}";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/api/users", HttpMethod.POST, request, String.class);
        assertThat(resp.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void readUser_token() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.getLoginToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/api/users/" + defaultUsername, HttpMethod.GET, request, String.class);
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void readOtherUser_token() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.getLoginToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/api/users/" + otherUsername, HttpMethod.GET, request, String.class);
        assertThat(resp.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void login_wrongClientToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.getRegToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/api/users/" + defaultUsername, HttpMethod.GET, request, String.class);
        assertThat(resp.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void register_then_login() {
        String username = "haolinwei2019@gmai.com";
        this.registerAnUser(username);
        restTemplate = restTemplate.withBasicAuth(env.getProperty("config.client.myprofile.id"), env.getProperty("config.client.myprofile.secret"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", "Password1!");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/oauth/token", HttpMethod.POST, request, String.class);
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
    }


    private String getLoginToken() {
        restTemplate = restTemplate.withBasicAuth(env.getProperty("config.client.myprofile.id"), env.getProperty("config.client.myprofile.secret"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", env.getProperty("config.user.embedded.username"));
        map.add("password", env.getProperty("config.user.embedded.pwd"));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        //NOTE: String.class make sure json response can be parsed correctly
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/oauth/token", HttpMethod.POST, request, String.class);
        return this.extractToken(resp);

    }

    private String getRegToken() {
        restTemplate = restTemplate.withBasicAuth(env.getProperty("config.client.register.id"), env.getProperty("config.client.register.secret"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> resp = restTemplate.exchange(host + randomServerPort + "/oauth/token", HttpMethod.POST, request, String.class);
        return this.extractToken(resp);
    }

    private String extractToken(ResponseEntity<String> resp) {
        ObjectMapper om = new ObjectMapper();
        om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            JsonNode nodes = om.readTree(resp.getBody());
            return nodes.get("access_token").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerAnUser(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.getRegToken());
        String jsonBody = "{\n" +
                "\t\"email\":\"" + username + "\",\n" +
                "\t\"password\":\"Password1!\"\n" +
                "}";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
        restTemplate.exchange(host + randomServerPort + "/api/users", HttpMethod.POST, request, String.class);
    }

}
