package com.mt.identityaccess.domain;

import com.mt.common.domain.model.domainId.UniqueIdGeneratorService;
import com.mt.common.serializer.CustomObjectSerializer;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import com.mt.identityaccess.domain.model.endpoint.EndpointRepository;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import com.mt.identityaccess.domain.model.revoke_token.RevokeTokenRepository;
import com.mt.identityaccess.domain.model.user.UserRepository;
import com.mt.identityaccess.domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    private static ClientRepository clientRepository;
    private static UserRepository userRepository;
    private static PendingUserRepository pendingUserRepository;
    private static EndpointRepository endpointRepository;
    private static EndpointService endpointService;
    private static EncryptionService encryptionService;
    private static AuthenticationService authenticationService;
    private static ClientService clientService;
    private static PendingUserService pendingUserService;
    private static UserService userService;
    private static UniqueIdGeneratorService uniqueIdGeneratorService;
    private static RevokeTokenService revokeTokenService;
    private static ActivationCodeService activationCodeService;
    private static PasswordResetTokenService passwordResetTokenService;
    private static RevokeTokenRepository revokeTokenRepository;
    private static CustomObjectSerializer objectSerializer;

    @Autowired
    public void setCustomObjectSerializer(CustomObjectSerializer customObjectSerializer) {
        DomainRegistry.objectSerializer = customObjectSerializer;
    }

    @Autowired
    public void setEndpointService(EndpointService endpointService) {
        DomainRegistry.endpointService = endpointService;
    }

    @Autowired
    public void setRevokeTokenRepository(RevokeTokenRepository revokeTokenRepository) {
        DomainRegistry.revokeTokenRepository = revokeTokenRepository;
    }

    @Autowired
    public void setActivationCodeService(ActivationCodeService activationCodeService) {
        DomainRegistry.activationCodeService = activationCodeService;
    }

    @Autowired
    public void setEndpointRepository(EndpointRepository endpointRepository) {
        DomainRegistry.endpointRepository = endpointRepository;
    }

    @Autowired
    public void setPasswordResetTokenService(PasswordResetTokenService passwordResetTokenService) {
        DomainRegistry.passwordResetTokenService = passwordResetTokenService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        DomainRegistry.userService = userService;
    }

    @Autowired
    public void setRevokeTokenService(RevokeTokenService revokeTokenService) {
        DomainRegistry.revokeTokenService = revokeTokenService;
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        DomainRegistry.clientRepository = clientRepository;
    }

    @Autowired
    public void setBizUserRepo(UserRepository bizUserRepo) {
        DomainRegistry.userRepository = bizUserRepo;
    }

    @Autowired
    public void setPendingUserRepo(PendingUserRepository pendingUserRepo) {
        DomainRegistry.pendingUserRepository = pendingUserRepo;
    }

    @Autowired
    public void setEncryptionService(EncryptionService encryptionService) {
        DomainRegistry.encryptionService = encryptionService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        DomainRegistry.authenticationService = authenticationService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        DomainRegistry.clientService = clientService;
    }

    @Autowired
    public void setPendingUserService(PendingUserService pendingUserService) {
        DomainRegistry.pendingUserService = pendingUserService;
    }

    @Autowired
    public void setUniqueIdGeneratorService(UniqueIdGeneratorService uniqueIdGeneratorService) {
        DomainRegistry.uniqueIdGeneratorService = uniqueIdGeneratorService;
    }

    public static ClientRepository clientRepository() {
        return clientRepository;
    }

    public static PendingUserRepository pendingUserRepository() {
        return pendingUserRepository;
    }

    public static UserRepository userRepository() {
        return userRepository;
    }

    public static EncryptionService encryptionService() {
        return encryptionService;
    }

    public static ClientService clientService() {
        return clientService;
    }

    public static PendingUserService pendingUserService() {
        return pendingUserService;
    }

    public static UniqueIdGeneratorService uniqueIdGeneratorService() {
        return uniqueIdGeneratorService;
    }

    public static RevokeTokenService revokeTokenService() {
        return revokeTokenService;
    }

    public static ActivationCodeService activationCodeService() {
        return activationCodeService;
    }

    public static CustomObjectSerializer customObjectSerializer() {
        return objectSerializer;
    }

    public static RevokeTokenRepository revokeTokenRepository() {
        return revokeTokenRepository;
    }

    public static AuthenticationService authenticationService() {
        return authenticationService;
    }

    public static EndpointService endpointService() {
        return endpointService;
    }

    public static EndpointRepository endpointRepository() {
        return endpointRepository;
    }

    public static PasswordResetTokenService passwordResetTokenService() {
        return passwordResetTokenService;
    }

    public static UserService userService() {
        return userService;
    }


}
