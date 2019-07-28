package com.hw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.interfaze.TokenRevocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;

@Component
public class ResourceOwnerTokenRevocationServiceImpl implements TokenRevocationService<ResourceOwner> {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${url.zuul.resourceOwner}")
    private String url;

    @Value("${feature.token.revocation}")
    private Boolean enabled;

    @Autowired
    private ObjectMapper mapper;

    /**
     * aspects: authority, lock
     * unlock a user should not revoke
     *
     * @param oldResourceOwner
     * @param newResourceOwner
     * @return
     */
    @Override
    public boolean shouldRevoke(ResourceOwner oldResourceOwner, ResourceOwner newResourceOwner) {
        if (!enabled) {
            return false;
        } else if (authorityChanged(oldResourceOwner, newResourceOwner)) {
            return true;
        } else if (lockUser(oldResourceOwner, newResourceOwner)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean lockUser(ResourceOwner oldResourceOwner, ResourceOwner newResourceOwner) {
        if (Boolean.TRUE.equals(newResourceOwner.getLocked())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean authorityChanged(ResourceOwner oldResourceOwner, ResourceOwner newResourceOwner) {
        HashSet<GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>> grantedAuthorities = new HashSet<>(oldResourceOwner.getGrantedAuthorities());
        HashSet<GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>> grantedAuthorities1 = new HashSet<>(newResourceOwner.getGrantedAuthorities());
        return !grantedAuthorities.equals(grantedAuthorities1);
    }

    @Override
    public void blacklist(String name, boolean shouldRevoke) {
        if (shouldRevoke && enabled) {
            HashMap<String, String> blockBody = new HashMap<>();
            blockBody.put("name", name);
            String body = null;
            try {
                body = mapper.writeValueAsString(blockBody);
            } catch (JsonProcessingException e) {
                /**
                 * this block is purposely left blank
                 */
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(body, headers);
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity, String.class);
        }
    }


}
