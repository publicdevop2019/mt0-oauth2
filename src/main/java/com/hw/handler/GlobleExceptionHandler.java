package com.hw.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class GlobleExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {TransactionSystemException.class,
            IllegalArgumentException.class,
            DataIntegrityViolationException.class,
            RedirectMismatchException.class,
    })
    protected ResponseEntity<?> handleException(RuntimeException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();

        String[] split = NestedExceptionUtils.getMostSpecificCause(ex).getMessage().replace("\t", "").split("\n");

        String s = UUID.randomUUID().toString();
        body.put("errors", split);
        body.put("error_id", s);
        log.error("Handled exception UUID - {} - class - [{}] - Exception :", s, ex.getClass(), ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Order()
    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<?> defaultHandleException(RuntimeException ex, WebRequest request) {
        /**
         * make sure default handler not affected
         */
        if (ex instanceof AccessDeniedException)
            throw ex;
        Map<String, Object> body = new LinkedHashMap<>();

        String[] split = NestedExceptionUtils.getMostSpecificCause(ex).getMessage().replace("\t", "").split("\n");
        String s = UUID.randomUUID().toString();
        body.put("errors", split);
        body.put("error_id", s);
        log.error("Unhandled exception UUID - {} - class - [{}] - Exception :", s, ex.getClass(), ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
