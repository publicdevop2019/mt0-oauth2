package com.mt.identityaccess.domain.model.client;

import com.mt.common.StringSetConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.Convert;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class AuthorizationCodeGrant extends AbstractGrant {

    @Convert(converter = StringSetConverter.class)
    @Getter
    private Set<String> redirectUrls;
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean autoApprove = false;

    public AuthorizationCodeGrant(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove, int accessTokenValiditySeconds) {
        super(grantTypes, accessTokenValiditySeconds);
        setRedirectUrls(redirectUrls);
        setAutoApprove(autoApprove);
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
