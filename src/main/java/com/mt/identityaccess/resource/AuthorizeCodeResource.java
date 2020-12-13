package com.mt.identityaccess.resource;

import com.mt.identityaccess.domain.model.authorize_code.AuthorizeCodeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthorizeCodeResource {

    @Autowired
    AuthorizeCodeApplicationService authorizeCodeService;

    @PostMapping("/authorize")
    public Map<String, String> authorize(@RequestParam Map<String, String> parameters, @RequestHeader("authorization") String authorization) {
        return authorizeCodeService.authorize(parameters, authorization);
    }
}
