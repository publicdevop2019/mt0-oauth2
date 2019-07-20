package com.hw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    /**
     * disable csrf required
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/v1/resourceOwner").permitAll()
                .antMatchers("/api/v1/resourceOwners").permitAll()
                .antMatchers("/api/v1/client").permitAll()
                .antMatchers("/api/v1/clients").permitAll()
                .antMatchers("/api/v1/authorize").permitAll()
                .antMatchers("/oauth/token_key").permitAll()
                .anyRequest().authenticated()
                .and()
                .anonymous().disable()
                .csrf().disable()
                .cors();
    }

    /**
     * used for password flow, explicitly set to enable password flow
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}