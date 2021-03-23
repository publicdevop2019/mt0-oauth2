package com.mt.access.domain.model.client;

import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
public class TokenDetail implements Serializable {

    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;

    public TokenDetail(int accessTokenValiditySeconds, int refreshTokenValiditySeconds) {
        setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    private void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        Validator.greaterThanOrEqualTo(accessTokenValiditySeconds, 60);
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    private void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        Validator.greaterThanOrEqualTo(refreshTokenValiditySeconds, 120);
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

}
