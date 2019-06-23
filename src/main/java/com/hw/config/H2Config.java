package com.hw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@Profile("h2")
public class H2Config extends ResourceServerConfigurerAdapter {

    private String[] ignoredPaths = {"/h2-console/**"};

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers(ignoredPaths).permitAll()
                .and()
                .headers().frameOptions().disable();

    }

}
