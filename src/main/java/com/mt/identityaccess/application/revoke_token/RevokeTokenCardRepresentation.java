package com.mt.identityaccess.application.revoke_token;

import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import lombok.Data;

@Data
public class RevokeTokenCardRepresentation {
    private String id;
    private String targetId;
    private Long issuedAt;
    private RevokeToken.TokenType type;

    public RevokeTokenCardRepresentation(Object object) {
        RevokeToken token = ((RevokeToken) object);
        id = token.getRevokeTokenId().getDomainId();
        targetId = token.getTargetId();
        issuedAt = token.getIssuedAt();
        type = token.getType();
    }
}
