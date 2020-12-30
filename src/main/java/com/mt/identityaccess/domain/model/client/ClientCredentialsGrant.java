package com.mt.identityaccess.domain.model.client;

import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
public class ClientCredentialsGrant extends AbstractGrant {

    @Override
    public GrantType name() {
        return GrantType.CLIENT_CREDENTIALS;
    }

    public ClientCredentialsGrant(Set<GrantType> grantTypes, int accessTokenValiditySeconds) {
        super(grantTypes, accessTokenValiditySeconds);
    }

}