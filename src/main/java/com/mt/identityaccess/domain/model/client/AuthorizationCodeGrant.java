package com.mt.identityaccess.domain.model.client;

import com.mt.common.StringSetConverter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.Convert;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class AuthorizationCodeGrant extends AbstractGrant {

    @Convert(converter = StringSetConverter.class)
    private Set<String> redirectUrls;

    private boolean autoApprove = false;

    public AuthorizationCodeGrant(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove, int accessTokenValiditySeconds) {
        super(grantTypes, accessTokenValiditySeconds);
        setRedirectUrls(redirectUrls);
        setAutoApprove(autoApprove);
    }

    public Set<String> redirectUrls() {
        return redirectUrls;
    }

    public boolean autoApprove() {
        return autoApprove;
    }


    private void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    private void setRedirectUrls(@Nullable Set<String> redirectUrls) {
        if (redirectUrls == null) {
            this.redirectUrls = new HashSet<>();
        } else {
            this.redirectUrls = new HashSet<>(redirectUrls);
        }
    }

    @Override
    public GrantType name() {
        return GrantType.AUTHORIZATION_CODE;
    }
}
