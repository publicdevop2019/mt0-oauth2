package com.mt.identityaccess.config;

import com.mt.identityaccess.application.client.ClientApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.util.Objects;

@Slf4j
@Component
public class BeanFactory {

    @Autowired
    ClientApplicationService clientApplicationService;

    /**
     * use Resource annotation to solve invoked before spring load issue
     */
    @Resource
    private Environment env;

    @Bean
    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {

        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();

        handler.setTokenStore(tokenStore);

        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientApplicationService));

        handler.setClientDetailsService(clientApplicationService);

        return handler;
    }

    @Bean
    public ApprovalStore approvalStore(TokenStore tokenStore) {

        TokenApprovalStore store = new TokenApprovalStore();

        store.setTokenStore(tokenStore);

        return store;
    }


    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {

        return new JwtTokenStore(accessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter(KeyPair keyPair) {

        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        converter.setKeyPair(keyPair);

        return converter;
    }

    @Bean
    public KeyPair getKeyPair() {

        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(Objects.requireNonNull(env.getProperty("jwt.key-store"))), Objects.requireNonNull(env.getProperty("jwt.password")).toCharArray());

        return keyStoreKeyFactory.getKeyPair(env.getProperty("jwt.alias"));
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices(TokenStore tokenStore) {

        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();

        defaultTokenServices.setTokenStore(tokenStore);

        defaultTokenServices.setSupportRefreshToken(true);

        return defaultTokenServices;
    }

    @Bean
    public InMemoryAuthorizationCodeServices authorizationCodeServices() {

        return new InMemoryAuthorizationCodeServices();
    }

    @Bean
    private DefaultOAuth2RequestFactory defaultOAuth2RequestFactory() {
        log.debug("loading DefaultOAuth2RequestFactory");
        return new DefaultOAuth2RequestFactory(clientApplicationService);
    }
}
