package com.mt.identityaccess.domain.model.client;

import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.BizClientAuthorityEnum;
import com.mt.identityaccess.domain.model.client.ScopeEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class BasicClientDetail {
    private String secret;
    public BasicClientDetail(String name, String clientSecret, String description, Set<ScopeEnum> scopeEnums, Set<BizClientAuthorityEnum> grantedAuthorities, Set<String> resourceIds) {
        this.secret= DomainRegistry.encryptionService().encryptedValue(clientSecret);
    }

    public BasicClientDetail(String name, String description, Set<ScopeEnum> scopeEnums, Set<BizClientAuthorityEnum> grantedAuthorities, Set<String> resourceIds) {

    }
}
