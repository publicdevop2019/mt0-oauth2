package com.hw.aggregate.user.model;

public enum BizUserAuthorityEnum {
    ROLE_ADMIN,
    ROLE_ROOT,
    ROLE_USER;

    public static class ResourceOwnerAuthorityConverter extends DefaultAuthorityConverter {
        public ResourceOwnerAuthorityConverter() {
            super(BizUserAuthorityEnum.class);
        }
    }

}
