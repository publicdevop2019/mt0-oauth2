package com.hw.controller;

import com.hw.service.ClientDetailsServiceImpl;
import com.hw.utility.ServiceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("v1/api")
public class AuthorizeCodeController {

    @Autowired
    ClientDetailsServiceImpl clientDetailsService;

    @Autowired
    private DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;

    private OAuth2RequestValidator oauth2RequestValidator = new DefaultOAuth2RequestValidator();

    private RedirectResolver redirectResolver = new DefaultRedirectResolver();

    @Autowired
    private InMemoryAuthorizationCodeServices authorizationCodeServices;

    @PostMapping("/authorize")
    public Map<String, String> authorize(@RequestParam Map<String, String> parameters, @RequestHeader("authorization") String authorization) {

        /**make sure authorize client exist*/

        if (clientDetailsService.loadClientByClientId(parameters.get(OAuth2Utils.CLIENT_ID)) == null)
            throw new IllegalArgumentException("unable to find authorize client");

        Authentication authentication = ServiceUtility.getAuthentication(authorization);

        AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(parameters);

        Set<String> responseTypes = authorizationRequest.getResponseTypes();

        if (!responseTypes.contains("token") && !responseTypes.contains("code")) {
            throw new UnsupportedResponseTypeException("Unsupported response types: " + responseTypes);
        }

        if (authorizationRequest.getClientId() == null) {
            throw new InvalidClientException("A client id must be provided");
        }


        ClientDetails client = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());

        String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
        String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
        if (!StringUtils.hasText(redirectUriParameter)) {
            throw new RedirectMismatchException(
                    "A redirectUri must be either supplied or preconfigured in the ClientDetails");
        }
        authorizationRequest.setRedirectUri(resolvedRedirect);

        oauth2RequestValidator.validateScope(authorizationRequest, client);

        authorizationRequest.setApproved(true);

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("authorize_code", generateCode(authorizationRequest, authentication));
        return stringStringHashMap;


    }

    private String generateCode(AuthorizationRequest authorizationRequest, Authentication authentication)
            throws AuthenticationException {

        try {

            OAuth2Request storedOAuth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);

            OAuth2Authentication combinedAuth = new OAuth2Authentication(storedOAuth2Request, authentication);

            return authorizationCodeServices.createAuthorizationCode(combinedAuth);

        } catch (OAuth2Exception e) {

            if (authorizationRequest.getState() != null) {
                e.addAdditionalInformation("state", authorizationRequest.getState());
            }

            throw e;

        }
    }
}
