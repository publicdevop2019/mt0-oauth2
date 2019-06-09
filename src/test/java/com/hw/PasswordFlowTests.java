package com.hw;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAuth2Service.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PasswordFlowTests {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private Environment env;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    //TODO: replace MockMvc with apache httpClient
    private MockMvc mockMvc;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    @Ignore
    //NOTE:this test is covered by getLoginToken also to resolve jenkins test issue
    public void obtainAccessToken_with_ValidUser() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", env.getProperty("config.oauth2.type"));
        params.add("username", env.getProperty("config.user.embedded.username"));
        params.add("password", env.getProperty("config.user.embedded.pwd"));
        mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(env.getProperty("config.client.myprofile.id"), env.getProperty("config.client.myprofile.secret")))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.access_token").exists())
        ;
    }

    @Test
    public void obtainAccessToken_with_inValidUsername() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", env.getProperty("config.oauth2.type"));
        params.add("username", env.getProperty("test.invalid.user.username"));
        params.add("password", env.getProperty("config.user.embedded.pwd"));
        mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(env.getProperty("config.client.myprofile.id"), env.getProperty("config.client.myprofile.secret")))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
        ;
    }

    @Test
    public void obtainAccessToken_with_inValidUserPwd() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", env.getProperty("config.oauth2.type"));
        params.add("username", env.getProperty("config.user.embedded.username"));
        params.add("password", env.getProperty("test.invalid.user.password"));
        mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(env.getProperty("config.client.myprofile.id"), env.getProperty("config.client.myprofile.secret")))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
        ;
    }

    @Test
    public void obtainAccessToken_with_inValidClientSecret() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", env.getProperty("config.oauth2.type"));
        params.add("username", env.getProperty("config.user.embedded.username"));
        params.add("password", env.getProperty("config.user.embedded.pwd"));
        ResultActions result =
                mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .with(httpBasic(env.getProperty("config.client.myprofile.id"), env.getProperty("test.invalid.client.secret")))
                        .accept("application/json;charset=UTF-8"))
                        .andExpect(status().isUnauthorized())
                //NOTE:response status is UNAUTHORIZED response body is lost
                //TODO: replace mockMvc with a HttpComponentsClientHttpRequestFactory to fix this
                //REF:https://github.com/spring-projects/spring-security-oauth/issues/441
                //.andExpect(content().contentType("application/json;charset=UTF-8"))
                ;
    }

    @Test
    public void cors_with_inValidClientID() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", env.getProperty("config.oauth2.type"));
        params.add("username", env.getProperty("config.user.embedded.username"));
        params.add("password", env.getProperty("config.user.embedded.pwd"));
        ResultActions result =
                mockMvc.perform(options("/oauth/token")
                        .params(params)
                        .with(httpBasic(env.getProperty("test.invalid.client.id"), env.getProperty("config.client.myprofile.secret")))
                        .accept("application/json;charset=UTF-8"))
                        .andExpect(status().isUnauthorized());
    }

}
