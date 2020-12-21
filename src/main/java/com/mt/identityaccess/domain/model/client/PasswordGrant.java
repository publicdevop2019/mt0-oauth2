package com.mt.identityaccess.domain.model.client;

import com.google.common.base.Objects;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import javax.persistence.Embedded;
import java.util.Set;

@NoArgsConstructor
public class PasswordGrant extends AbstractGrant {

    private void setRefreshTokenGrant(RefreshTokenGrant refreshTokenGrant) {
        this.refreshTokenGrant = refreshTokenGrant;
    }

    @Embedded
    private RefreshTokenGrant refreshTokenGrant;

    public PasswordGrant(Set<GrantType> grantTypes, int accessTokenValiditySeconds, RefreshTokenGrant refreshTokenGrant) {
        super(grantTypes, accessTokenValiditySeconds);
        setRefreshTokenGrant(refreshTokenGrant);
    }

    public RefreshTokenGrant refreshTokenGrant() {
        return refreshTokenGrant;
    }

    public static void detectChange(@Nullable PasswordGrant a, @Nullable PasswordGrant b, ClientId clientId) {
        if (!ObjectUtils.equals(a, b)) {
            AbstractGrant.detectChange(a, b, clientId);
            if (a == null && b == null) {
            } else if (a == null) {
                RefreshTokenGrant.detectChange(null, b.refreshTokenGrant(), clientId);
            } else if (b == null) {
                RefreshTokenGrant.detectChange(a.refreshTokenGrant(), null, clientId);
            } else {
                RefreshTokenGrant.detectChange(a.refreshTokenGrant(), b.refreshTokenGrant(), clientId);
            }
        }
    }

    @Override
    public GrantType name() {
        return GrantType.PASSWORD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordGrant)) return false;
        if (!super.equals(o)) return false;
        PasswordGrant that = (PasswordGrant) o;
        return Objects.equal(refreshTokenGrant, that.refreshTokenGrant);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), refreshTokenGrant);
    }
}
