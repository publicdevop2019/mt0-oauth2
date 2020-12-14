package com.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan({"com.hw","com.mt","com.hw.shared.idempotent"})
public class OAuth2 {
    public static void main(String[] args) {
        SpringApplication.run(OAuth2.class, args);
    }
}

