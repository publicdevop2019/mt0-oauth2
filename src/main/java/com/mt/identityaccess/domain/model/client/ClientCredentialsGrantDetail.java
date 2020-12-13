package com.mt.identityaccess.domain.model.client;

import java.util.Set;

public class ClientCredentialsGrantDetail {
    private GrantType grantType;

    public GrantType getGrantType() {
        return grantType;
    }

    public ClientCredentialsGrantDetail(Set<GrantType> grantTypes) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.CLIENT_CREDENTIALS)).findFirst().orElse(null));
    }

    private void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }
}
