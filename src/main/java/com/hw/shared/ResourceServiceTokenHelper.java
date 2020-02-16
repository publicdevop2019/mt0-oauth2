package com.hw.shared;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class ResourceServiceTokenHelper {
    @Value("${security.oauth2.client.accessTokenUri:#{null}}")
    private String tokenUrl;

    @Value("${security.oauth2.client.clientId:#{null}}")
    private String clientId;
    @Value("${security.oauth2.client.clientSecret:#{null}}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    public String storedJwtToken = null;

    public String getJwtToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth(clientId, clientSecret);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        String token = null;
        try {

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> resp = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);
            token = this.extractToken(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
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
}