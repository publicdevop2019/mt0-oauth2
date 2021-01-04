package com.mt.identityaccess.application.revoke_token;

import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RevokeTokenCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String id;
    private RevokeToken.TokenType type;

    public RevokeTokenCreateCommand(String id, RevokeToken.TokenType type) {
        this.id = id;
        this.type = type;
    }
}
