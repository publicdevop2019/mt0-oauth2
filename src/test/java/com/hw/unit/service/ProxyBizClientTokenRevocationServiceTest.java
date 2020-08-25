package com.hw.unit.service;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import com.hw.aggregate.client.model.GrantTypeEnum;
import com.hw.aggregate.client.model.ScopeEnum;
import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.ProxyBizClientTokenRevocationService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

public class ProxyBizClientTokenRevocationServiceTest {


    ProxyBizClientTokenRevocationService clientTokenRevocationServiceImpl = new ProxyBizClientTokenRevocationService();

//    @Test
//    public void test_shouldRevoke_authority_same() {
//        String s = UUID.randomUUID().toString();
//        BizClient client = getClient(s);
//        BizClient client2 = getClient(s);
//        GrantedAuthorityImpl authority = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
//        GrantedAuthorityImpl authority2 = getAuthority(BizClientAuthorityEnum.ROLE_FIRST_PARTY);
//        GrantedAuthorityImpl authority3 = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
//        GrantedAuthorityImpl authority4 = getAuthority(BizClientAuthorityEnum.ROLE_FIRST_PARTY);
//        client.setGrantedAuthorities(Arrays.asList(authority, authority2));
//        client2.setGrantedAuthorities(Arrays.asList(authority3, authority4));
//        boolean b = clientTokenRevocationServiceImpl.shouldRevoke(client, client2);
//        Assert.assertEquals(false, b);
//
//    }
//
//    @Test
//    public void test_shouldRevoke_authority_same_order_diff() {
//        String s = UUID.randomUUID().toString();
//        BizClient client = getClient(s);
//        BizClient client2 = getClient(s);
//        GrantedAuthorityImpl authority = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
//        GrantedAuthorityImpl authority2 = getAuthority(BizClientAuthorityEnum.ROLE_FIRST_PARTY);
//        GrantedAuthorityImpl authority3 = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
//        GrantedAuthorityImpl authority4 = getAuthority(BizClientAuthorityEnum.ROLE_FIRST_PARTY);
//        client.setGrantedAuthorities(Arrays.asList(authority, authority2));
//        client2.setGrantedAuthorities(Arrays.asList(authority4, authority3));
//        boolean b = clientTokenRevocationServiceImpl.shouldRevoke(client, client2);
//        Assert.assertEquals(false, b);
//
//    }
//
//    @Test
//    public void test_shouldRevoke_authority_diff() {
//        String s = UUID.randomUUID().toString();
//        BizClient client = getClient(s);
//        BizClient client2 = getClient(s);
//        GrantedAuthorityImpl authority = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
//        GrantedAuthorityImpl authority2 = getAuthority(BizClientAuthorityEnum.ROLE_THIRD_PARTY);
//        GrantedAuthorityImpl authority3 = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
//        GrantedAuthorityImpl authority4 = getAuthority(BizClientAuthorityEnum.ROLE_FIRST_PARTY);
//        client.setGrantedAuthorities(Arrays.asList(authority, authority2));
//        client2.setGrantedAuthorities(Arrays.asList(authority3, authority4));
//        boolean b = clientTokenRevocationServiceImpl.shouldRevoke(client, client2);
//        Assert.assertEquals(true, b);
//    }
//
//    @Test
//    public void test_shouldRevoke_scope_same() {
//        String s = UUID.randomUUID().toString();
//        BizClient client = getClient(s);
//        BizClient client2 = getClient(s);
//        client.setScopeEnums(new HashSet<>(Arrays.asList(ScopeEnum.read, ScopeEnum.write)));
//        client2.setScopeEnums(new HashSet<>(Arrays.asList(ScopeEnum.read, ScopeEnum.write)));
//        boolean b = clientTokenRevocationServiceImpl.shouldRevoke(client, client2);
//        Assert.assertEquals(false, b);
//    }
//
//    @Test
//    public void test_shouldRevoke_scope_diff() {
//        String s = UUID.randomUUID().toString();
//        BizClient client = getClient(s);
//        BizClient client2 = getClient(s);
//        client.setScopeEnums(new HashSet<>(Arrays.asList(ScopeEnum.read, ScopeEnum.write)));
//        client2.setScopeEnums(new HashSet<>(Arrays.asList(ScopeEnum.read, ScopeEnum.trust)));
//        boolean b = clientTokenRevocationServiceImpl.shouldRevoke(client, client2);
//        Assert.assertEquals(true, b);
//    }
//
//    @Test
//    public void test_shouldRevoke_resource_indicator() {
//        String s = UUID.randomUUID().toString();
//        BizClient client = getClient(s);
//        BizClient client2 = getClient(s);
//        client.setResourceIndicator(false);
//        client2.setResourceIndicator(true);
//        boolean b = clientTokenRevocationServiceImpl.shouldRevoke(client, client2);
//        Assert.assertEquals(false, b);
//    }
//
//    @Test
//    public void test_shouldRevoke_redirectUrl_same() {
//        String s = UUID.randomUUID().toString();
//        BizClient client = getClient(s);
//        BizClient client2 = getClient(s);
//        client.setRegisteredRedirectUri(Collections.EMPTY_SET);
//        boolean b = clientTokenRevocationServiceImpl.shouldRevoke(client, client2);
//        Assert.assertEquals(false, b);
//    }

    private BizClient getClient(String clientId) {
        BizClient client = new BizClient();
        client.setClientId(clientId);
        /** set default to prevent NPE*/
        client.setScopeEnums(new HashSet<>(Collections.singletonList(ScopeEnum.read)));
        GrantedAuthorityImpl authority = getAuthority(BizClientAuthorityEnum.ROLE_BACKEND);
        client.setGrantedAuthorities(Collections.singletonList(authority));
        client.setGrantTypeEnums(new HashSet<>(Collections.singletonList(GrantTypeEnum.client_credentials)));
        client.setAccessTokenValiditySeconds(1000);
        client.setResourceIds(new HashSet<>(Collections.singletonList("dummyResourceId")));
        return client;
    }

    private GrantedAuthorityImpl getAuthority(BizClientAuthorityEnum clientAuthorityEnum) {
        return GrantedAuthorityImpl.getGrantedAuthority(BizClientAuthorityEnum.class, clientAuthorityEnum.toString());
    }

}