package com.mt.identityaccess.port.adapter.http;

import com.mt.common.logging.ErrorMessage;
import com.mt.identityaccess.application.client.InvalidClientIdException;
import com.mt.identityaccess.application.client.RootClientDeleteException;
import com.mt.identityaccess.application.user.RootUserDeleteException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            RedirectMismatchException.class,
            AccessDeniedException.class,
            RootClientDeleteException.class,
            RootUserDeleteException.class,
            IllegalArgumentException.class,
            OAuth2Exception.class,
            InvalidClientIdException.class
    })
    protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMessage(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
