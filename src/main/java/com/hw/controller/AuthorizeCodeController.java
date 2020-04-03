package com.hw.controller;

import com.hw.service.AuthorizeCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthorizeCodeController {

    @Autowired
    AuthorizeCodeServiceImpl authorizeCodeService;

    @PostMapping("/authorize")
    public Map<String, String> authorize(@RequestParam Map<String, String> parameters, @RequestHeader("authorization") String authorization) {
        return authorizeCodeService.authorize(parameters, authorization);
    }
}
