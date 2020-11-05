package com.hw.shared.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class BizValidator {
    @Autowired
    private RestTemplate restTemplate;

    public void validate(String name, Object command) {
        String resolvedUrl = "http://localhost:4040/" + name;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> hashMapHttpEntity = new HttpEntity<>(command, headers);
        ParameterizedTypeReference<List<ValidatorMessage>> responseType = new ParameterizedTypeReference<>() {
        };
        try {
            ResponseEntity<List<ValidatorMessage>> exchange = restTemplate.exchange(resolvedUrl, HttpMethod.POST, hashMapHttpEntity, responseType);
            if (Objects.requireNonNull(exchange.getBody()).size() != 0) {
                log.debug("error from validator {}", exchange.getBody());
                throw new ValidationFailedException();
            }
        } catch (RestClientException ex) {
            throw new ValidationErrorException(ex);
        }

    }
}
