package com.mt.identityaccess.domain.model;

import com.mt.identityaccess.domain.service.AuthenticationService;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import com.mt.identityaccess.domain.service.ClientService;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import com.mt.identityaccess.domain.model.user.UserRepository;
import com.mt.identityaccess.domain.service.EncryptionService;
import com.mt.identityaccess.domain.service.RevokeTokenService;
import com.mt.identityaccess.domain.service.UniqueIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    private static ClientRepository clientRepository;
    private static UserRepository bizUserRepo;
    private static PendingUserRepository pendingUserRepo;
    private static EncryptionService encryptionService;
    private static AuthenticationService authenticationService;
    private static ClientService clientService;
    private static UniqueIdGeneratorService uniqueIdGeneratorService;
    private static RevokeTokenService revokeTokenService;

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
        DomainRegistry.bizUserRepo = bizUserRepo;
    }

    @Autowired
    public void setPendingUserRepo(PendingUserRepository pendingUserRepo) {
        DomainRegistry.pendingUserRepo = pendingUserRepo;
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
    public void setUniqueIdGeneratorService(UniqueIdGeneratorService uniqueIdGeneratorService) {
        DomainRegistry.uniqueIdGeneratorService = uniqueIdGeneratorService;
    }

    public static ClientRepository clientRepository() {
        return clientRepository;
    }

    public static EncryptionService encryptionService() {
        return encryptionService;
    }

    public static ClientService clientService() {
        return clientService;
    }

    public static UniqueIdGeneratorService uniqueIdGeneratorService() {
        return uniqueIdGeneratorService;
    }

    public static RevokeTokenService revokeTokenService() {
        return revokeTokenService;
    }
}
