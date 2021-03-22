package com.mt.access.domain.model.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.TypedClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Lob;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AuthorizationCodeGrant extends AbstractGrant implements Serializable {

    @Lob
    @Getter
    @Convert(converter = RedirectURLConverter.class)
    private final Set<RedirectURL> redirectUrls = new HashSet<>();
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean autoApprove = false;
    private static ObjectMapper om;

    @Autowired
    public void setOM(ObjectMapper om2) {
        AuthorizationCodeGrant.om = om2;
    }

    public AuthorizationCodeGrant(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove, int accessTokenValiditySeconds) {
        super(grantTypes, accessTokenValiditySeconds);
        if (redirectUrls != null) {
            setRedirectUrls(redirectUrls.stream().map(RedirectURL::new).collect(Collectors.toSet()));
        }
        setAutoApprove(autoApprove);
    }

    private void setRedirectUrls(Set<RedirectURL> redirectUrls) {
        this.redirectUrls.clear();
        this.redirectUrls.addAll(redirectUrls);
    }

    @Override
    public GrantType name() {
        return GrantType.AUTHORIZATION_CODE;
    }

    private static class RedirectURLConverter implements AttributeConverter<Set<RedirectURL>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<RedirectURL> redirectURLS) {
            if (redirectURLS == null || redirectURLS.isEmpty())
                return null;
            return CommonDomainRegistry.getCustomObjectSerializer().serializeCollection(redirectURLS).getBytes();
        }

        @Override
        public Set<RedirectURL> convertToEntityAttribute(byte[] bytes) {
            if (bytes == null || bytes.length == 0)
                return Collections.emptySet();
            Collection<RedirectURL> redirectURLS = CommonDomainRegistry.getCustomObjectSerializer().deserializeCollection(new String(bytes, StandardCharsets.UTF_8), RedirectURL.class);
            return new HashSet<>(redirectURLS);
        }
    }
}
