package com.mt.identityaccess.domain.model;

import com.mt.identityaccess.domain.model.client.ClientService;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import com.mt.identityaccess.domain.model.user.UserRepository;
import com.mt.identityaccess.application.AuthenticationApplicationService;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    private static ClientRepository clientRepository;
    private static UserRepository bizUserRepo;
    private static PendingUserRepository pendingUserRepo;
    private static EncryptionService encryptionService;
    private static AuthenticationApplicationService authenticationService;
    private static ClientService clientService;

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
    public void setAuthenticationService(AuthenticationApplicationService authenticationService) {
        DomainRegistry.authenticationService = authenticationService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        DomainRegistry.clientService = clientService;
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
}
