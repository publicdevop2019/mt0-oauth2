package com.mt.identityaccess.domain.model.client;


import com.mt.identityaccess.port.adapter.service.EnumSetConverter;

public enum Authority {
    ROLE_FRONTEND,
    ROLE_BACKEND,
    ROLE_FIRST_PARTY,
    ROLE_THIRD_PARTY,
    ROLE_TRUST,
    ROLE_ROOT;

    public static class AuthorityConverter extends EnumSetConverter<Authority> {
        public AuthorityConverter() {
            super(Authority.class);
        }
    }
}
