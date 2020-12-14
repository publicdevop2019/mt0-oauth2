package com.hw.domain.model.client;


import com.hw.port.adapter.service.EnumSetConverter;

public enum Authority {
    ROLE_FRONTEND,
    ROLE_BACKEND,
    ROLE_FIRST_PARTY,
    ROLE_THIRD_PARTY,
    ROLE_TRUST,
    /**
     * root client can not be deleted
     */
    ROLE_ROOT;

    public static class ClientAuthorityConverter extends EnumSetConverter<Authority> {
        public ClientAuthorityConverter() {
            super(Authority.class);
        }
    }
}
