package com.mt.identityaccess.domain.model.revoke_token;

import com.mt.common.audit.Auditable;
import com.mt.common.persistence.EnumConverter;
import com.mt.common.rest.Aggregate;
import com.mt.identityaccess.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
@Slf4j
@NoArgsConstructor
public class RevokeToken extends Auditable implements Aggregate {
    public static final String ENTITY_TARGET_ID = "targetId";
    public static final String ENTITY_ISSUE_AT = "issuedAt";
    @Id
    private Long id;
    @Column(nullable = false)
    private String targetId;
    @Column(nullable = false)
    private Long issuedAt;

    @Column(nullable = false)
    @Embedded
    private RevokeTokenId revokeTokenId;
    @Convert(converter = TokenType.DBConverter.class)
    @Column(nullable = false)
    private TokenType type;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public RevokeToken(String targetId, RevokeTokenId revokeTokenId, TokenType type) {
        this.id = DomainRegistry.uniqueIdGeneratorService().id();
        this.targetId = targetId;
        this.issuedAt = new Date().getTime();
        this.revokeTokenId = revokeTokenId;
        this.type = type;
    }

    public enum TokenType {
        CLIENT,
        USER;

        public static class DBConverter extends EnumConverter<TokenType> {
            public DBConverter() {
                super(TokenType.class);
            }
        }
    }
}
