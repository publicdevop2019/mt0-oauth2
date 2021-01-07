package com.mt.identityaccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mt"})
@EnableJpaRepositories(basePackages = {"com.mt"})
@EntityScan("com.mt")
@EnableCaching
public class IdentityAccess {
    public static void main(String[] args) {
        SpringApplication.run(IdentityAccess.class, args);
    }
}

