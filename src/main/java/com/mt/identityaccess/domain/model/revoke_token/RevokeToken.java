package com.mt.identityaccess.domain.model.revoke_token;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Slf4j
@NoArgsConstructor
public class RevokeToken {
    public static final String ENTITY_TARGET_ID = "targetId";
    public static final String ENTITY_ISSUE_AT = "issuedAt";
    private String targetId;
    private Long issuedAt;
    private RevokeTokenId revokeTokenId;
    private TokenType type;

    public RevokeToken(String targetId, RevokeTokenId revokeTokenId, TokenType type) {
        this.targetId = targetId;
        this.issuedAt = new Date().getTime();
        this.revokeTokenId = revokeTokenId;
        this.type = type;
    }

    public enum TokenType {
        CLIENT,
        USER;
    }
}
