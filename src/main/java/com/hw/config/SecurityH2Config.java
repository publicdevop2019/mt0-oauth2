package com.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Profile("shared-hw")
public class SecurityH2Config extends WebSecurityConfigurerAdapter {
    /**
     * used for password flow, explicitly set to enable password flow
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
        ;

    }

}