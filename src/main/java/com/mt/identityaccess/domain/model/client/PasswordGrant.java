package com.mt.identityaccess.domain.model.client;

import lombok.*;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import javax.persistence.Embedded;
import java.util.Set;

@NoArgsConstructor
@EqualsAndHashCode
public class PasswordGrant extends AbstractGrant {

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private RefreshTokenGrant refreshTokenGrant;

    public PasswordGrant(Set<GrantType> grantTypes, int accessTokenValiditySeconds, RefreshTokenGrant refreshTokenGrant) {
        super(grantTypes, accessTokenValiditySeconds);
        setRefreshTokenGrant(refreshTokenGrant);
    }

    public static void detectChange(@Nullable PasswordGrant a, @Nullable PasswordGrant b, ClientId clientId) {
        if (!ObjectUtils.equals(a, b)) {
            AbstractGrant.detectChange(a, b, clientId);
            if (a == null && b == null) {
            } else if (a == null) {
                RefreshTokenGrant.detectChange(null, b.getRefreshTokenGrant(), clientId);
            } else if (b == null) {
                RefreshTokenGrant.detectChange(a.getRefreshTokenGrant(), null, clientId);
            } else {
                RefreshTokenGrant.detectChange(a.getRefreshTokenGrant(), b.getRefreshTokenGrant(), clientId);
            }
        }
    }

    @Override
    public GrantType name() {
        return GrantType.PASSWORD;
    }
}
