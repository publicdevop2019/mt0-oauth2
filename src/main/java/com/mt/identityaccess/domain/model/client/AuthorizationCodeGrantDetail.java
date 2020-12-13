package com.mt.identityaccess.domain.model.client;

import java.util.Set;

public class AuthorizationCodeGrantDetail {
    public GrantType getGrantType() {
        return grantType;
    }

    private GrantType grantType;
    private Set<String> redirectUrls;
    private boolean autoApprove = false;

    public AuthorizationCodeGrantDetail(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove) {
        this.setGrantType(grantTypes.stream().filter(e -> e.equals(GrantType.AUTHORIZATION_CODE)).findFirst().orElse(null));
        this.setRedirectUrls(redirectUrls);
        this.setAutoApprove(autoApprove);
    }

    public void setAutoApprove(boolean autoApprove) {
        if (GrantType.AUTHORIZATION_CODE.equals(grantType))
            this.autoApprove = autoApprove;
    }


    public void setRedirectUrls(Set<String> redirectUrls) {
        if (GrantType.AUTHORIZATION_CODE.equals(grantType))
            this.redirectUrls = redirectUrls;
    }

    public void setGrantType(GrantType grantType) {
        if (!GrantType.AUTHORIZATION_CODE.equals(grantType)) {
            this.redirectUrls = null;
        }
        this.grantType = grantType;
    }
}
