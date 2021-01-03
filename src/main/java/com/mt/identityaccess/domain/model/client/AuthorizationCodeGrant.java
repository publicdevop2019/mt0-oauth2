package com.mt.identityaccess.domain.model.client;

import com.mt.identityaccess.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Lob;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AuthorizationCodeGrant extends AbstractGrant {

    @Lob
    @Getter
    @Convert(converter = RedirectURLConverter.class)
    private Set<RedirectURL> redirectUrls;
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean autoApprove = false;

    public AuthorizationCodeGrant(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove, int accessTokenValiditySeconds) {
        super(grantTypes, accessTokenValiditySeconds);
        if (redirectUrls == null) {
            setRedirectUrls(Collections.emptySet());
        } else {
            setRedirectUrls(redirectUrls.stream().map(RedirectURL::new).collect(Collectors.toSet()));
        }
        setAutoApprove(autoApprove);
    }

    private void setRedirectUrls(Set<RedirectURL> redirectUrls) {
        this.redirectUrls = new HashSet<>(redirectUrls);
    }

    @Override
    public GrantType name() {
        return GrantType.AUTHORIZATION_CODE;
    }

    private static class RedirectURLConverter implements AttributeConverter<Set<RedirectURL>,byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<RedirectURL> redirectURLS) {
            return DomainRegistry.customObjectSerializer().nativeSerialize(redirectURLS);
        }

        @Override
        public Set<RedirectURL> convertToEntityAttribute(byte[] bytes) {
            return (Set<RedirectURL>)DomainRegistry.customObjectSerializer().nativeDeserialize(bytes);
        }
    }
}
