package com.mt.identityaccess.domain.model.user;

import com.mt.common.validate.Validator;
import lombok.Getter;

public class CurrentPassword {
    @Getter
    private final String rawPassword;

    public CurrentPassword(String rawPassword) {
        Validator.notNull(rawPassword);
        Validator.notBlank(rawPassword);
        this.rawPassword = rawPassword;
    }
}
