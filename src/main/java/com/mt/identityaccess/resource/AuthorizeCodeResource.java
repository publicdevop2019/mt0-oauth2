package com.mt.identityaccess.resource;

import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.infrastructure.JwtAuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.mt.common.AppConstant.HTTP_HEADER_AUTHORIZATION;

@RestController
public class AuthorizeCodeResource {

    @PostMapping("/authorize")
    public Map<String, String> authorize(@RequestParam Map<String, String> parameters, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        return ApplicationServiceRegistry.authorizeCodeApplicationService().authorize(parameters);
    }
}
