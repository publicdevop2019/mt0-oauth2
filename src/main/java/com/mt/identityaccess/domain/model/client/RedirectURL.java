package com.mt.identityaccess.domain.model.client;

import lombok.Getter;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.Serializable;

public class RedirectURL implements Serializable {
    private static final long serialVersionUID = 1;
    private static final UrlValidator defaultValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
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
