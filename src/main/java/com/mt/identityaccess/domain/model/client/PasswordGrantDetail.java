package com.mt.identityaccess.domain.model.client;

import java.util.Set;

public class PasswordGrantDetail {
    private GrantType grantType;

    public GrantType getGrantType() {
        return grantType;
    }

    public PasswordGrantDetail(Set<GrantType> grantTypes) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.PASSWORD)).findFirst().orElse(null));
    }

    private void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }
}
