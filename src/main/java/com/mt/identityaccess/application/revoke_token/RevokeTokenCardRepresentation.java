package com.mt.identityaccess.application.revoke_token;

import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import lombok.Data;

@Data
public class RevokeTokenCardRepresentation {
    private String targetId;
    private Long issuedAt;
    private RevokeToken.TokenType type;

    public RevokeTokenCardRepresentation(Object object) {
        RevokeToken token = ((RevokeToken) object);
        targetId = token.getRevokeTokenId().getDomainId();
        issuedAt = token.getIssuedAt();
        type = token.getRevokeTokenId().getType();
    }
}
