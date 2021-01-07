package com.mt.identityaccess.application.user;

import lombok.Getter;

public class UserQuery {
    @Getter
    private String value;

    public UserQuery(String queryParam) {
        this.value = queryParam;
    }

}
