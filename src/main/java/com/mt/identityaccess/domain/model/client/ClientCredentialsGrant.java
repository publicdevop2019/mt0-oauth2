package com.mt.identityaccess.domain.model.client;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
public class ClientCredentialsGrant extends AbstractGrant  implements Serializable {

    @Override
    public GrantType name() {
        return GrantType.CLIENT_CREDENTIALS;
    }

    public ClientCredentialsGrant(Set<GrantType> grantTypes, int accessTokenValiditySeconds) {
        super(grantTypes, accessTokenValiditySeconds);
    }

}
