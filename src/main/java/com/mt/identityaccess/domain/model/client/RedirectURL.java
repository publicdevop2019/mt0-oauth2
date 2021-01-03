package com.mt.identityaccess.domain.model.client;

import lombok.Getter;
import org.apache.commons.validator.routines.UrlValidator;

public class RedirectURL {
    private static final UrlValidator defaultValidator = new UrlValidator();
    @Getter
    private final String value;

    public RedirectURL(String url) {
        if (defaultValidator.isValid(url)) {
            value = url;
        } else {
            throw new InvalidRedirectURLException();
        }
    }

    public static class InvalidRedirectURLException extends RuntimeException {
    }
}
